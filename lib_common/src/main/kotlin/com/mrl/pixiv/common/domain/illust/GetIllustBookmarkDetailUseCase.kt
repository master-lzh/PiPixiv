package com.mrl.pixiv.common.domain.illust

import com.mrl.pixiv.common.data.illust.IllustBookmarkDetailResp
import com.mrl.pixiv.common.network.safeHttpCall
import com.mrl.pixiv.common.repository.IllustRepository
import org.koin.core.annotation.Single

@Single
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