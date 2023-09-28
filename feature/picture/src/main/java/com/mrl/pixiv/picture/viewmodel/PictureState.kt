package com.mrl.pixiv.picture.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mrl.pixiv.common.data.State
import com.mrl.pixiv.data.Illust

data class PictureState(
    val illustRelated: SnapshotStateList<Illust>,
    val userIllusts: SnapshotStateList<Illust>,
    val isBookmark: Boolean,
    val nextUrl: String,
) : State {
    companion object {
        val INITIAL = PictureState(
            illustRelated = mutableStateListOf(),
            userIllusts = mutableStateListOf(),
            isBookmark = false,
            nextUrl = ""
        )
    }
}