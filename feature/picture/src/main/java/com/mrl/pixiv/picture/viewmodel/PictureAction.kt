package com.mrl.pixiv.picture.viewmodel

import android.graphics.Bitmap
import com.mrl.pixiv.common.viewmodel.Action
import com.mrl.pixiv.data.Illust

sealed class PictureAction : Action {
    data class GetIllustDetail(val illustId: Long) : PictureAction()
    data class AddSearchHistory(val keyword: String) : PictureAction()
    data class GetUserIllustsIntent(
        val userId: Long,
    ) : PictureAction()

    data class GetIllustRelatedIntent(
        val illustId: Long,
    ) : PictureAction()

    data class LoadMoreIllustRelatedIntent(
        val queryMap: Map<String, String>? = null,
    ) : PictureAction()

    data class BookmarkIllust(val illustId: Long) : PictureAction()

    data class UnBookmarkIllust(val illustId: Long) : PictureAction()

    data class DownloadUgoira(val illustId: Long) : PictureAction()

    data class UpdateState(val state: PictureState) : PictureAction()

    data class UpdateUserIllustsState(val userIllusts: List<Illust>) :
        PictureAction()

    data class UpdateIllustRelatedState(val illustRelated: List<Illust>, val nextUrl: String) :
        PictureAction()

    data class UpdateIsBookmarkState(
        val userIllusts: List<Illust>,
        val illustRelated: List<Illust>
    ) : PictureAction()

    data class UpdateIllust(val illust: Illust) : PictureAction()

    data class UpdateUgoiraFrame(val images: List<Pair<Bitmap, Long>>) : PictureAction()

    data class DownloadIllust(
        val illustId: Long,
        val index: Int,
        val originalUrl: String,
        val downloadCallback: (result: Boolean) -> Unit
    ) : PictureAction()
}