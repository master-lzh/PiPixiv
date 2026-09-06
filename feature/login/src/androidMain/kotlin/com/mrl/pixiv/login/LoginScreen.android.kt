package com.mrl.pixiv.login

import android.annotation.SuppressLint
import android.webkit.WebSettings
import dev.nucleusframework.webview.web.NativeWebView

@SuppressLint("SetJavaScriptEnabled")
internal actual fun NativeWebView.setUp() {
    settings.apply {
        javaScriptEnabled = true
        cacheMode = WebSettings.LOAD_DEFAULT
        userAgentString = userAgentString.replace("; wv", "")
    }
}
