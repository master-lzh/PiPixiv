package com.mrl.pixiv.common.domain.illust

import com.mrl.pixiv.common.coroutine.launchProcess
import com.mrl.pixiv.common.coroutine.withIOContext
import com.mrl.pixiv.common.data.illust.IllustBookmarkDetailResp
import com.mrl.pixiv.common.repository.PixivRepository

object GetIllustBookmarkDetailUseCase{
    operator fun invoke(illustId: Long, onSuccess: (IllustBookmarkDetailResp) -> Unit) =
        launchProcess {
            val resp = withIOContext { PixivRepository.getIllustBookmarkDetail(illustId) }
            onSuccess(resp)
        }
}