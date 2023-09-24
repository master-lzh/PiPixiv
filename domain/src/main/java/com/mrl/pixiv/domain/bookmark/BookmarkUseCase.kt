package com.mrl.pixiv.domain.bookmark

import com.mrl.pixiv.common.coroutine.launchIO
import com.mrl.pixiv.common.network.safeHttpCall
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.domain.R
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import com.mrl.pixiv.util.ToastUtil

class BookmarkUseCase(
    private val illustRemoteRepository: IllustRemoteRepository,
) {
    operator fun invoke(illustBookmarkAddReq: IllustBookmarkAddReq, onSuccess: () -> Unit) {
        launchIO {
            safeHttpCall(
                request = illustRemoteRepository.postIllustBookmarkAdd(illustBookmarkAddReq),
                failedCallback = {
                    ToastUtil.safeShortToast(R.string.bookmark_add_failed)
                }
            ) {
                onSuccess()
                ToastUtil.safeShortToast(R.string.bookmark_add_success)
            }
        }
    }
}