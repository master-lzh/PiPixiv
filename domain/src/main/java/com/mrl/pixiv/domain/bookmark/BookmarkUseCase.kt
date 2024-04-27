package com.mrl.pixiv.domain.bookmark

import com.mrl.pixiv.common.network.safeHttpCall
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.domain.R
import com.mrl.pixiv.repository.IllustRepository
import com.mrl.pixiv.util.ToastUtil

class BookmarkUseCase(
    private val illustRepository: IllustRepository,
) {
    suspend operator fun invoke(illustBookmarkAddReq: IllustBookmarkAddReq, onSuccess: () -> Unit) {
        safeHttpCall(
            request = illustRepository.postIllustBookmarkAdd(illustBookmarkAddReq),
            failedCallback = {
                ToastUtil.safeShortToast(R.string.bookmark_add_failed)
            }
        ) {
            onSuccess()
            ToastUtil.safeShortToast(R.string.bookmark_add_success)
        }
    }
}