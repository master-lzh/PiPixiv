package com.mrl.pixiv.picture

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mrl.pixiv.common.util.throttleClick
import platform.Photos.PHAccessLevelReadWrite
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHPhotoLibrary
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
internal actual fun Modifier.clickWithPermission(onClick: () -> Unit): Modifier =
    this.throttleClick {
        PHPhotoLibrary.requestAuthorizationForAccessLevel(PHAccessLevelReadWrite) { status ->
            if (status == PHAuthorizationStatusAuthorized || status == PHAuthorizationStatusLimited) {
                dispatch_async(dispatch_get_main_queue()) {
                    onClick()
                }
            }
        }
    }
