package com.mrl.pixiv.common.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import okio.Closeable
import kotlin.coroutines.CoroutineContext

class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }
}