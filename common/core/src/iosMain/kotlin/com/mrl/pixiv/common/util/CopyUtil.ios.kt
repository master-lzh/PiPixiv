package com.mrl.pixiv.common.util

import platform.UIKit.UIPasteboard

actual suspend fun copyToClipboard(text: String) {
    UIPasteboard.generalPasteboard.string = text
}

actual suspend fun readTextFromClipboard(): String? = UIPasteboard.generalPasteboard.string
