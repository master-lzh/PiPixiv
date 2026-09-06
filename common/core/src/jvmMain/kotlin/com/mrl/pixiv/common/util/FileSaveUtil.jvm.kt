package com.mrl.pixiv.common.util

import dev.nucleusframework.window.tao.XdgPortalParent
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogParent
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.openFileSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal actual suspend fun platformSelectSaveFile(
    suggestedName: String,
    defaultExtension: String?,
): PlatformFile? {
    val window = desktopWindowServices().window
    var portalParent: XdgPortalParent? = null
    try {
        val parent = when (platform) {
            Platform.Desktop.Windows -> FileKitDialogParent.windows(window.nativeHandle)
            Platform.Desktop.Linux -> {
                // Assign before crossing the dispatcher boundary back so cancellation
                // cannot discard an acquired Wayland export before finally can release it.
                withContext(Dispatchers.IO) { portalParent = window.xdgPortalParent() }
                when (val resolved = portalParent) {
                    is XdgPortalParent.X11 -> FileKitDialogParent.x11(resolved.xid)
                    is XdgPortalParent.Wayland -> FileKitDialogParent.wayland(resolved.handle)
                    null -> error("The XDG desktop portal could not acquire the application window.")
                }
            }
            // FileKit 0.15 opens a native NS(Save/Open)Panel, but does not yet
            // accept a borrowed NSWindow parent. Never construct an AWT owner.
            else -> null
        }
        return FileKit.openFileSaver(
            suggestedName = suggestedName,
            defaultExtension = defaultExtension,
            // A Linux native parent also prevents FileKit's AWT fallback from
            // creating a second window system if the portal is unavailable.
            dialogSettings = FileKitDialogSettings(parent = parent),
        )
    } finally {
        (portalParent as? XdgPortalParent.Wayland)?.close()
    }
}
