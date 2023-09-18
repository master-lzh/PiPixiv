package com.mrl.pixiv.common_viewmodel.bookmark

import com.mrl.pixiv.common.base.BaseViewModel
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BookmarkViewModel(
    private val userLocalRepository: UserLocalRepository,
    private val illustRemoteRepository: IllustRemoteRepository,
) : BaseViewModel() {
    private val _bookmarkStatus = MutableStateFlow<Boolean?>(null)
    val bookmarkStatus = _bookmarkStatus.asStateFlow()

    fun bookmarkIllust(id: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkAdd(IllustBookmarkAddReq(id))
            ) {
                if (it!=null) {
                    _bookmarkStatus.value = true
                }
            }
        }
    }

    fun deleteBookmarkIllust(id: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRemoteRepository.postIllustBookmarkDelete(IllustBookmarkDeleteReq(id))
            ) {
                if (it!=null) {
                    _bookmarkStatus.value = false
                }
            }
        }
    }
}