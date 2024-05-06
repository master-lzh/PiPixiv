package com.mrl.pixiv.picture.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mrl.pixiv.common.viewmodel.State
import com.mrl.pixiv.data.Illust
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class PictureState(
    val illust: Illust?,
    val illustRelated: SnapshotStateList<Illust>,
    val userIllusts: SnapshotStateList<Illust>,
    val nextUrl: String,
    val ugoiraImages: ImmutableList<Pair<Bitmap, Long>>,
) : State {
    companion object {
        val INITIAL = PictureState(
            illust = null,
            illustRelated = mutableStateListOf(),
            userIllusts = mutableStateListOf(),
            nextUrl = "",
            ugoiraImages = persistentListOf()
        )
    }
}