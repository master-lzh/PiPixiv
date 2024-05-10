package com.mrl.pixiv.domain.illust

import com.mrl.pixiv.common.network.safeHttpCall
import com.mrl.pixiv.data.illust.IllustBookmarkDetailResp
import com.mrl.pixiv.repository.IllustRepository

class GetIllustBookmarkDetailUseCase(
    private val illustRepository: IllustRepository,
) {
    suspend operator fun invoke(illustId: Long, onSuccess: (IllustBookmarkDetailResp) -> Unit) =
        safeHttpCall(
            request = illustRepository.getIllustBookmarkDetail(illustId),
        ) {
            onSuccess(it)
        }
}