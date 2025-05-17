# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn dalvik.system.CloseGuard


-assumenosideeffects public class androidx.compose.runtime.ComposerKt {

   boolean isTraceInProgress();

   void traceEventStart(int,int,int,java.lang.String);

   void traceEventEnd();

}

# 对于使用了 @Keep 注解的类和成员，不进行混淆处理
-keep @androidx.annotation.Keep class * {

}

# 保持 Kotlin 标准库不被混淆
-keep class kotlin.concurrent.** { *; }
-keep class kotlin.coroutines.** { *; }
-dontwarn kotlin.coroutines.**

# 如果使用了 Kotlin 协程
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.impl.StaticMDCBinder
-dontwarn org.koin.ksp.generated.*

-keep class de.jensklingenberg.ktorfit.** { *; }
-keepclassmembers class de.jensklingenberg.ktorfit.** { *; }

-keep class com.mrl.pixiv.common.network.ApiClient
-keep class com.mrl.pixiv.common.network.AuthClient
-keep class com.mrl.pixiv.common.network.ImageClient