package com.mrl.pixiv.domain.bookmark

import com.mrl.pixiv.common.network.safeHttpCall
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.domain.R
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import com.mrl.pixiv.util.ToastUtil

class UnBookmarkUseCase(
    private val illustRemoteRepository: IllustRemoteRepository,
) {
    suspend operator fun invoke(
        illustBookmarkDeleteReq: IllustBookmarkDeleteReq,
        onSuccess: () -> Unit
    ) {
        safeHttpCall(
            request = illustRemoteRepository.postIllustBookmarkDelete(illustBookmarkDeleteReq),
            failedCallback = {
                ToastUtil.safeShortToast(R.string.bookmark_delete_failed)
            }
        ) {
            onSuccess()
            ToastUtil.safeShortToast(R.string.bookmark_delete_success)
        }
    }
}