package com.mrl.pixiv.picture.viewmodel

import com.mrl.pixiv.common.data.Action
import com.mrl.pixiv.data.Illust

sealed class PictureAction : Action {
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

    data class UpdateState(val state: PictureState) : PictureAction()

    data class UpdateUserIllustsState(val userIllusts: List<Illust>) :
        PictureAction()

    data class UpdateIllustRelatedState(val illustRelated: List<Illust>, val nextUrl: String) :
        PictureAction()

    data class UpdateIsBookmarkState(
        val userIllusts: List<Illust>,
        val illustRelated: List<Illust>
    ) : PictureAction()
}