[CmdletBinding()]
param(
    [string]$MsiPath = 'composeApp/build/compose/binaries/main-release/msi/*.msi',
    [string]$OutputDirectory = 'composeApp/build/reports/windows-startup'
)

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest

# This installs into the runner's real per-user location. Never run it on a
# developer machine or a persistent self-hosted runner.
if ($env:OS -ne 'Windows_NT' -or $env:GITHUB_ACTIONS -ne 'true' -or
    $env:RUNNER_ENVIRONMENT -ne 'github-hosted') {
    throw 'This startup check only runs on disposable GitHub-hosted Windows runners.'
}

$output = [System.IO.Path]::GetFullPath($OutputDirectory)
New-Item -ItemType Directory -Path $output -Force | Out-Null
$installLog = Join-Path $output 'msi-install.log'
$stdoutLog = Join-Path $output 'app-stdout.log'
$stderrLog = Join-Path $output 'app-stderr.log'
foreach ($log in @($installLog, $stdoutLog, $stderrLog)) {
    New-Item -ItemType File -Path $log -Force | Out-Null
}

$result = [ordered]@{
    status = 'failed'
    startedAt = [DateTime]::UtcNow.ToString('o')
    msiPath = $MsiPath
    msiSha256 = $null
    installDirectory = $null
    installerExitCode = $null
    nativeLibrary = $null
    processId = $null
    processExitCode = $null
    windowReadySeconds = $null
    stableSeconds = 30
    observedStableSeconds = $null
    window = $null
    screenshot = $null
    screenshotError = $null
    error = $null
    cleanupError = $null
    finishedAt = $null
}
$application = $null
$installer = $null
$windowHandle = [IntPtr]::Zero
$checks = [System.Collections.Generic.List[object]]::new()

try {
    $packages = @(Get-Item -Path $MsiPath)
    if ($packages.Count -ne 1 -or $packages[0].Extension -ne '.msi') {
        throw 'MsiPath must resolve to exactly one MSI package.'
    }
    $package = $packages[0].FullName
    $result.msiPath = $package
    $result.msiSha256 = (Get-FileHash -LiteralPath $package -Algorithm SHA256).Hash
    $installDirectory = Join-Path $env:LOCALAPPDATA 'PiPixiv'
    $result.installDirectory = $installDirectory
    if (Test-Path -LiteralPath $installDirectory) {
        throw "Refusing to replace an existing installation at $installDirectory."
    }

    Add-Type -TypeDefinition @'
using System;
using System.Runtime.InteropServices;
using System.Text;
public static class PiPixivStartupWindow {
    [StructLayout(LayoutKind.Sequential)]
    public struct Rect { public int Left, Top, Right, Bottom; }
    [DllImport("user32.dll", CharSet = CharSet.Unicode)]
    public static extern int GetClassName(IntPtr hwnd, StringBuilder name, int maxCount);
    [DllImport("user32.dll")]
    [return: MarshalAs(UnmanagedType.Bool)]
    public static extern bool IsWindow(IntPtr hwnd);
    [DllImport("user32.dll")]
    [return: MarshalAs(UnmanagedType.Bool)]
    public static extern bool IsWindowVisible(IntPtr hwnd);
    [DllImport("user32.dll")]
    [return: MarshalAs(UnmanagedType.Bool)]
    public static extern bool GetWindowRect(IntPtr hwnd, out Rect rect);
}
'@

    # Use the MSI's normal per-user installation path, without staging files.
    $installArguments = '/i "{0}" /qn /norestart /L*V "{1}" ALLUSERS="" MSIINSTALLPERUSER=1' -f $package, $installLog
    $installer = Start-Process -FilePath (Join-Path $env:SystemRoot 'System32/msiexec.exe') `
        -ArgumentList $installArguments -PassThru
    if (-not $installer.WaitForExit(300000)) {
        throw 'MSI installation did not complete within five minutes.'
    }
    $installer.Refresh()
    $result.installerExitCode = $installer.ExitCode
    if ($installer.ExitCode -notin @(0, 3010)) {
        throw "MSI installation failed with exit code $($installer.ExitCode)."
    }

    $executable = Join-Path $installDirectory 'PiPixiv.exe'
    $library = Join-Path $installDirectory 'app/resources/composeResources/files/mmkv/mmkvc.dll'
    if (-not (Test-Path -LiteralPath $executable -PathType Leaf)) {
        throw "Installed executable does not exist: $executable"
    }
    if (-not (Test-Path -LiteralPath $library -PathType Leaf)) {
        throw "Installed MMKV native library does not exist: $library"
    }
    $libraryInfo = Get-Item -LiteralPath $library
    if ($libraryInfo.Length -eq 0) {
        throw "Installed MMKV native library is empty: $library"
    }
    $result.nativeLibrary = [ordered]@{
        path = $library
        bytes = $libraryInfo.Length
        sha256 = (Get-FileHash -LiteralPath $library -Algorithm SHA256).Hash
    }

    $startup = [System.Diagnostics.Stopwatch]::StartNew()
    $application = Start-Process -FilePath $executable -WorkingDirectory $installDirectory `
        -RedirectStandardOutput $stdoutLog -RedirectStandardError $stderrLog -PassThru
    $result.processId = $application.Id
    while ($startup.Elapsed.TotalSeconds -lt 60) {
        $application.Refresh()
        if ($application.HasExited) {
            $result.processExitCode = $application.ExitCode
            throw "PiPixiv exited before its main window appeared (exit code $($application.ExitCode))."
        }
        $handle = $application.MainWindowHandle
        $windowClass = [System.Text.StringBuilder]::new(256)
        if ($handle -ne [IntPtr]::Zero) {
            $windowHandle = $handle
            [PiPixivStartupWindow]::GetClassName($handle, $windowClass, $windowClass.Capacity) | Out-Null
        }
        $check = [ordered]@{
            elapsedSeconds = [Math]::Round($startup.Elapsed.TotalSeconds, 2)
            handle = $handle.ToInt64()
            title = $application.MainWindowTitle
            class = $windowClass.ToString()
            responding = $application.Responding
        }
        $checks.Add($check)
        if ($handle -ne [IntPtr]::Zero -and $check.title -ceq 'PiPixiv' -and
            $check.class -ceq 'SunAwtFrame' -and $check.responding -and
            [PiPixivStartupWindow]::IsWindowVisible($handle)) {
            $result.windowReadySeconds = $check.elapsedSeconds
            $result.window = $check
            break
        }
        Start-Sleep -Seconds 1
    }
    if ($null -eq $result.window) {
        throw 'No responding PiPixiv SunAwtFrame main window appeared within 60 seconds.'
    }

    # MMKV initializes synchronously before application()/Window in main.kt.
    # A launcher error dialog cannot satisfy the title and AWT class checks.
    $stability = [System.Diagnostics.Stopwatch]::StartNew()
    do {
        $application.Refresh()
        if ($application.HasExited) {
            $result.processExitCode = $application.ExitCode
            throw "PiPixiv exited during the stability check (exit code $($application.ExitCode))."
        }
        if ($application.MainWindowHandle -ne $windowHandle -or
            -not [PiPixivStartupWindow]::IsWindow($windowHandle) -or
            -not [PiPixivStartupWindow]::IsWindowVisible($windowHandle) -or
            $application.MainWindowTitle -cne 'PiPixiv' -or -not $application.Responding) {
            throw 'The PiPixiv main window disappeared, changed, or stopped responding.'
        }
        if ($stability.Elapsed.TotalSeconds -ge $result.stableSeconds) { break }
        Start-Sleep -Seconds 1
    } while ($true)
    $result.observedStableSeconds = [Math]::Round($stability.Elapsed.TotalSeconds, 2)
    $result.status = 'passed'
}
catch {
    $result.error = $_.Exception.Message
    Write-Warning $result.error
}
finally {
    # Capture only the app window, before stopping the process. Screenshot
    # failures are diagnostic and must not hide the actual startup result.
    if ($windowHandle -ne [IntPtr]::Zero) {
        $bitmap = $null
        $graphics = $null
        try {
            $rectangle = [PiPixivStartupWindow+Rect]::new()
            if (-not [PiPixivStartupWindow]::IsWindow($windowHandle) -or
                -not [PiPixivStartupWindow]::GetWindowRect($windowHandle, [ref]$rectangle)) {
                throw 'The application window is no longer available for a screenshot.'
            }
            Add-Type -AssemblyName System.Drawing
            $width = $rectangle.Right - $rectangle.Left
            $height = $rectangle.Bottom - $rectangle.Top
            if ($width -le 0 -or $height -le 0) { throw 'The application window has no visible area.' }
            $bitmap = [System.Drawing.Bitmap]::new($width, $height)
            $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
            $graphics.CopyFromScreen($rectangle.Left, $rectangle.Top, 0, 0, $bitmap.Size)
            $screenshot = Join-Path $output 'main-window.png'
            $bitmap.Save($screenshot, [System.Drawing.Imaging.ImageFormat]::Png)
            $result.screenshot = $screenshot
        }
        catch {
            $result.screenshotError = $_.Exception.Message
            Write-Warning "Window screenshot failed: $($result.screenshotError)"
        }
        finally {
            if ($null -ne $graphics) { $graphics.Dispose() }
            if ($null -ne $bitmap) { $bitmap.Dispose() }
        }
    }

    # Keep process objects from Start-Process; never stop processes by name.
    foreach ($ownedProcess in @($application, $installer)) {
        if ($null -eq $ownedProcess) { continue }
        try {
            $ownedProcess.Refresh()
            if (-not $ownedProcess.HasExited) {
                $ownedProcess.Kill()
                $ownedProcess.WaitForExit(10000) | Out-Null
            }
            $ownedProcess.Dispose()
        }
        catch {
            $result.cleanupError = $_.Exception.Message
            Write-Warning "Owned-process cleanup failed: $($result.cleanupError)"
        }
    }
    $result.finishedAt = [DateTime]::UtcNow.ToString('o')
    ConvertTo-Json -InputObject ($checks.ToArray()) -Depth 5 | Set-Content -LiteralPath (Join-Path $output 'window-checks.json') -Encoding utf8
    $result | ConvertTo-Json -Depth 5 | Tee-Object -FilePath (Join-Path $output 'result.json')
}

if ($result.status -ne 'passed') { exit 1 }
