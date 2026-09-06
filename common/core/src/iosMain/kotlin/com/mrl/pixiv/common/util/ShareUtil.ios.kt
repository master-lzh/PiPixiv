package com.mrl.pixiv.common.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSURL
import platform.Photos.PHAsset
import platform.Photos.PHImageContentModeDefault
import platform.Photos.PHImageManager
import platform.Photos.PHImageManagerMaximumSize
import platform.Photos.PHImageRequestOptions
import platform.Photos.PHImageRequestOptionsDeliveryModeHighQualityFormat
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIImage
import platform.UIKit.UIWindowScene
import platform.UIKit.popoverPresentationController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual object ShareUtil {
    actual suspend fun shareText(text: String) {
        val scene = currentWindowScene() ?: return
        presentItems(listOf(text), scene)
    }

    /**
     * 分享指定的图片到其他应用。
     *
     * @param imageUri ph://<PHAsset.localIdentifier>。
     */
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun shareImage(imageUri: String) {
        // Keep the originating scene even if loading the Photos asset finishes after a scene switch.
        val scene = currentWindowScene() ?: return
        if (imageUri.startsWith("ph://")) {
            val localIdentifier = imageUri.removePrefix("ph://")
            val fetchResult = PHAsset.fetchAssetsWithLocalIdentifiers(listOf(localIdentifier), null)
            val asset = fetchResult.firstObject as? PHAsset

            if (asset != null) {
                val options = PHImageRequestOptions()
                options.networkAccessAllowed = true
                options.deliveryMode = PHImageRequestOptionsDeliveryModeHighQualityFormat

                PHImageManager.defaultManager().requestImageForAsset(
                    asset,
                    targetSize = PHImageManagerMaximumSize.readValue(),
                    contentMode = PHImageContentModeDefault,
                    options = options
                ) { image, _ ->
                    if (image is UIImage) {
                        presentItems(listOf(image), scene)
                    }
                }
            }
        } else {
            val url = NSURL.fileURLWithPath(imageUri)
            presentItems(listOf(url), scene)
        }
    }

    private suspend fun currentWindowScene(): UIWindowScene? =
        withContext(Dispatchers.Main.immediate) {
            getCurrentViewController()?.view?.window?.windowScene
        }

    @OptIn(ExperimentalForeignApi::class)
    private fun presentItems(items: List<Any>, scene: UIWindowScene) {
        dispatch_async(dispatch_get_main_queue()) {
            val topController = getCurrentViewController(scene) ?: return@dispatch_async
            val controller = UIActivityViewController(items, null)
            controller.popoverPresentationController?.apply {
                sourceView = topController.view
                sourceRect = topController.view.bounds.useContents {
                    CGRectMake(origin.x + size.width / 2, origin.y + size.height / 2, 0.0, 0.0)
                }
            }
            topController.presentViewController(controller, true, null)
        }
    }
}
