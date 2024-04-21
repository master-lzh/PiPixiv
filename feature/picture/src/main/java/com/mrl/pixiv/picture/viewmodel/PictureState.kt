package com.mrl.pixiv.picture.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust

@Stable
data class PictureState(
    val illust: Illust?,
    val illustRelated: SnapshotStateList<Illust>,
    val userIllusts: SnapshotStateList<Illust>,
    val nextUrl: String,
) : State {
    companion object {
        val INITIAL = PictureState(
            illust = null,
            illustRelated = mutableStateListOf(),
            userIllusts = mutableStateListOf(),
            nextUrl = ""
        )
    }
}