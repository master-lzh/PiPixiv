package com.mrl.pixiv.common.viewmodel.bookmark

import androidx.compose.runtime.mutableStateMapOf
import com.mrl.pixiv.common.coroutine.launchProcess
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.repository.PixivRepository
import kotlinx.coroutines.Dispatchers

val requireBookmarkState
    get() = BookmarkState.state

object BookmarkState {
    internal val state = mutableStateMapOf<Long, Boolean>()

    fun bookmarkIllust(
        illustId: Long,
        restrict: String = Restrict.PUBLIC,
        tags: List<String>? = null
    ) {
        launchProcess(Dispatchers.IO) {
            PixivRepository.postIllustBookmarkAdd(illustId, restrict, tags)
            state[illustId] = true
        }
    }

    fun deleteBookmarkIllust(illustId: Long) {
        launchProcess(Dispatchers.IO) {
            PixivRepository.postIllustBookmarkDelete(illustId)
            state[illustId] = false
        }
    }
}