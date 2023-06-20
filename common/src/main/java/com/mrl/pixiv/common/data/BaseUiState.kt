package com.mrl.pixiv.common.data

abstract class BaseUiState {
    var success: Boolean = false
    var loading: Boolean = true
    var failure: Throwable? = null
}

fun <S : BaseUiState> S.success() =
    apply {
        success = true
        loading = false
    }

fun <S : BaseUiState> S.failed(throwable: Throwable) =
    apply {
        success = false
        loading = false
        failure = throwable
    }