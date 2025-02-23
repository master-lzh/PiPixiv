package com.mrl.pixiv.common.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val ioScope by lazy { CoroutineScope(Dispatchers.IO) }
private val mainScope by lazy { CoroutineScope(Dispatchers.Main) }


fun launchIO(block: suspend CoroutineScope.() -> Unit) {
    ioScope.launch(start = CoroutineStart.DEFAULT, block = block)
}

fun launchNetwork(
    onError: (Throwable) -> Unit = {},
    onComplete: () -> Unit = {},
    block: suspend CoroutineScope.() -> Unit,
) {
    try {
        launchIO(block)
    } catch (e: Throwable) {
        onError(e)
    } finally {
        onComplete()
    }
}

fun launchUI(block: suspend CoroutineScope.() -> Unit): Job =
    mainScope.launch(start = CoroutineStart.DEFAULT, block = block)

fun launchNow(block: suspend CoroutineScope.() -> Unit): Job =
    mainScope.launch(start = CoroutineStart.UNDISPATCHED, block = block)

fun CoroutineScope.launchUI(block: suspend CoroutineScope.() -> Unit): Job =
    launch(Dispatchers.Main, block = block)

fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit): Job =
    launch(Dispatchers.IO, block = block)

fun CoroutineScope.launchNonCancellable(block: suspend CoroutineScope.() -> Unit): Job =
    launchIO { withContext(NonCancellable, block) }

suspend fun <T> withUIContext(block: suspend CoroutineScope.() -> T) = withContext(
    Dispatchers.Main,
    block,
)

suspend fun <T> withIOContext(block: suspend CoroutineScope.() -> T) = withContext(
    Dispatchers.IO,
    block,
)

suspend fun <T> withNonCancellableContext(block: suspend CoroutineScope.() -> T) =
    withContext(NonCancellable, block)
