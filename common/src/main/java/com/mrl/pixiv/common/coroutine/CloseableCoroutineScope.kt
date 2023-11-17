package com.mrl.pixiv.common.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import okio.Closeable
import kotlin.coroutines.CoroutineContext

/** [CoroutineScope] implementing [Closeable] that cancels the scope on closure. */
class CloseableCoroutineScope(
    context: CoroutineContext,
) : Closeable, CoroutineScope {
    /** @{inheritDoc} */
    override val coroutineContext: CoroutineContext = context

    /** @{inheritDoc} */
    override fun close() = coroutineContext.cancel()
}