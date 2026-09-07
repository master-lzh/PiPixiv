package com.mrl.pixiv

import com.github.panpf.zoomimage.util.setMainThreadChecker
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.EmptyCoroutineContext

internal fun configureZoomImageMainThreadChecker() {
    // Tao owns Compose's UI thread. ZoomImage's desktop default only recognizes Swing EDT.
    // Consult the dispatcher on each call: Tao binds its thread when its native loop starts.
    setMainThreadChecker {
        !Dispatchers.Main.immediate.isDispatchNeeded(EmptyCoroutineContext)
    }
}
