package com.mrl.pixiv.common.util

import platform.UIKit.UIApplication
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene

fun getCurrentViewController(windowScene: UIWindowScene? = null): UIViewController? {
    val scenes = windowScene?.let(::listOf)
        ?: UIApplication.sharedApplication.connectedScenes.filterIsInstance<UIWindowScene>()
    val keyWindow = scenes
        .filter { it.activationState == UISceneActivationStateForegroundActive }
        .firstNotNullOfOrNull { it.keyWindow }
    var rootViewController = keyWindow?.rootViewController

    while (rootViewController?.presentedViewController != null) {
        rootViewController = rootViewController.presentedViewController
    }

    return rootViewController
}
