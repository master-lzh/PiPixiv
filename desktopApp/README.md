# desktopApp

纯 Kotlin/JVM 桌面启动模块，依赖 `composeApp` 的共享 UI、导航及 JVM 平台实现。
本模块拥有 Nucleus/Tao 窗口、图标、ProGuard、Sentry mapping 和原生安装包；
Android/iOS 继续使用 `composeApp`，iOS IPA 任务的位置不变。

MMKV 原生库生成到 `build/generated/desktopResources`，再复制进安装包。
构建过程不再向共享移动端 source 目录写入桌面二进制。
应用名称、FileKit appId、macOS bundle ID、Windows MSI UpgradeCode 均保留。

## 构建与运行

从项目根目录执行：

```sh
./gradlew :desktopApp:run
./gradlew :desktopApp:hotRunAsync
./gradlew :desktopApp:test -Pdebug=false
./gradlew :desktopApp:packageReleaseDmg :desktopApp:uploadDesktopSentryMapping -Pdebug=false
./gradlew :desktopApp:packageReleaseMsi :desktopApp:uploadDesktopSentryMapping -Pdebug=false
./gradlew :desktopApp:createReleaseDistributable :desktopApp:uploadDesktopSentryMapping -Pdebug=false
```

安装包与 mapping 输出根目录为 `desktopApp/build/compose/binaries/main-release`。
上传 mapping 仍通过 `SENTRY_AUTH_TOKEN` 和现有 CLI 配置认证。

## Java 25 AOT

AOT 保留原来的应用 JAR 和 bundled JVM，额外生成 `app.aot`，主要用于启动和预热优化；
它不是 GraalVM Native Image，通常会增加安装包体积。默认关闭，通过 `-PdesktopAot=true` 开启：

```sh
./gradlew :desktopApp:packageReleaseDmg :desktopApp:uploadDesktopSentryMapping -Pdebug=false -PdesktopAot=true
./gradlew :desktopApp:packageReleaseMsi :desktopApp:uploadDesktopSentryMapping -Pdebug=false -PdesktopAot=true
# Linux：验证完成后再归档镜像
./gradlew :desktopApp:verifyReleaseAotCache :desktopApp:uploadDesktopSentryMapping -Pdebug=false -PdesktopAot=true
```

无 `DISPLAY` 的 Linux 环境需要安装 Xvfb；Nucleus 的训练任务会启动虚拟显示服务。

训练使用经过混淆及 Sentry UUID 注入的最终镜像。入口检测 `nucleus.aot.mode=training`，
仅加载有限类及方法签名，不初始化应用单例、Koin、用户数据、网络、Sentry 或窗口，随后正常退出。
这是有限的类加载训练；没有完整 UI 启动基准时，不能承诺具体启动加速幅度。

训练前删除本镜像的旧缓存，避免旧文件掩盖训练失败。`verifyReleaseAotCache` 使用镜像自身的 launcher，
以 `-XX:AOTMode=on` 强制验证新缓存。开启 AOT 的 DMG/MSI 打包依赖该检查。
不要以 `run -Paot=train` 替代上述流程，它没有自动设置应用的隔离训练模式。

缓存必须匹配最终 JAR、JDK build、系统和 CPU 架构；依赖、混淆输出、metadata 或 JRE 变更后需要重新训练。
默认 compatibility 模式避免 CPU 特定 adapter 缓存，但仍可能保存类元数据、heap 对象和方法 profile，JIT 仍然运行。

来源：[Java 25 AOT 参数](https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html)、
[AOT 运行一致性](https://inside.java/2026/01/09/run-aot-cache/)。

### 本项目实测

2026-09-06，macOS arm64、OpenJDK 25.0.2、Nucleus 2.5.14，使用本次拆分后的相同源码、
混淆 JAR 和 Sentry mapping UUID 对照构建（`-Pdebug=false -PapplyFirebasePlugins=true`）：

| 配置 | DMG 字节数 | DMG 大小 |
| --- | ---: | ---: |
| 默认，关闭 AOT | 96,984,814 | 92.49 MiB |
| `-PdesktopAot=true` | 99,830,821 | 95.21 MiB |
| 增量 | +2,846,007 | +2.71 MiB（+2.93%） |

AOT 缓存本身为 11,255,808 字节（10.73 MiB）。两次构建的应用 JAR SHA-256 均为
`0864fe439e83ce340c7e5137ec82b3fa631b547c3ef43849a0cdf2ac123dd7de`。
除了构建目录内的检查，还从只读挂载的 DMG 启动实际 launcher，以 `-XX:AOTMode=on`
确认迁移后的安装路径能够加载缓存并正常退出。这里没有测量完整 UI 启动耗时。

这是本机 AOT 开关的直接对照，不是 GitHub 2.4.0 旧发布包的重建；
不能将该增量直接套用到 Windows/Linux 或其他 JDK build。

## MSI 与 NSIS

本轮保留 MSI。NSIS 可以包装相同 JVM/Tao 应用，但不能直接接管现有 MSI：

- 旧 MSI 的产品登记、卸载和快捷方式需要显式迁移；NSIS 不会自动识别现有 UpgradeCode。
- 已发布客户端的 Windows 更新资产筛选为 `.msi`，直接只发布 `.exe` 会中断其更新下载。
- 当前 MSI 默认在 `%LOCALAPPDATA%/Programs/PiPixiv/PiPixiv`，NSIS 向导模式默认在其父目录。
  直接并装会出现嵌套目录及递归卸载误删问题，需要阻止共存或设计迁移流程。
- FileKit appId 保留即可保留独立的 MMKV/应用 token；但 WebView2 默认浏览器 profile 随 exe 路径变化，
  需要稳定的浏览器数据目录及必要的 profile 迁移，避免登录页会话重置。
- CI 当前使用 MSI COM、ComponentPath 和 msiexec；NSIS 需要独立安装验证分支。企业部署的 MSI 检测及修复规则也需调整。

Nucleus 更新器本身同时支持 MSI 和 NSIS，不要求为了自动更新更换安装格式。
当前项目仍使用自己的更新管理器；跨格式升级还需处理安装目录变化后的重启目标。

来源：[Nucleus 2.5.14 配置生成器](https://github.com/NucleusFramework/Nucleus/blob/v2.5.14/plugin-build/plugin/src/main/kotlin/dev/nucleusframework/desktop/application/internal/electronbuilder/ElectronBuilderConfigGenerator.kt)、
[electron-builder NSIS](https://www.electron.build/v26/docs/nsis/)、
[WebView2 用户数据目录](https://learn.microsoft.com/en-us/microsoft-edge/webview2/concepts/user-data-folder)、
[Nucleus 更新器](https://nucleusframework.dev/en/docs/packaging/auto-update/)。
