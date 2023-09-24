package com.mrl.pixiv.picture.viewmodel

import com.mrl.pixiv.common.data.State
import com.mrl.pixiv.data.Illust

data class PictureState(
    val illustRelated: List<Illust>,
    val userIllusts: List<Illust>,
    val isBookmark: Boolean,
    val nextUrl: String,
) : State {
    companion object {
        val INITIAL = PictureState(
            illustRelated = emptyList(),
            userIllusts = emptyList(),
            isBookmark = false,
            nextUrl = ""
        )
    }
}