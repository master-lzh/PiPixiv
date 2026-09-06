package com.mrl.pixiv.common.util

actual object ShareUtil {
    actual suspend fun shareText(text: String) {
        copyToClipboard(text)
    }

    actual suspend fun shareImage(imageUri: String) {
        copyImageToClipboard(imageUri)
    }
}
