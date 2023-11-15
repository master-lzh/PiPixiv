package com.mrl.pixiv.common.network

import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.util.NetworkExceptionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

suspend fun <T> safeHttpCall(
    request: Flow<Rlt<T>>,
    failedCallback: suspend (Throwable) -> Unit = { NetworkExceptionUtil.resolveException(it) },
    successCallback: (T) -> Unit,
) {
    request.flowOn(Dispatchers.Main)
        .catch {
            failedCallback(it)
        }.collect {
            when (it) {
                is Rlt.Success -> successCallback(it.data)
                is Rlt.Failed -> failedCallback(it.error.exception)
            }
        }
}