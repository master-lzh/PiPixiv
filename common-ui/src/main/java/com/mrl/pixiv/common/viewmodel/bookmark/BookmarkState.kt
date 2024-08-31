package com.mrl.pixiv.common.viewmodel.bookmark

import com.mrl.pixiv.common.coroutine.launchNetwork
import com.mrl.pixiv.common.viewmodel.GlobalState
import com.mrl.pixiv.data.Restrict
import com.mrl.pixiv.data.illust.IllustBookmarkAddReq
import com.mrl.pixiv.data.illust.IllustBookmarkDeleteReq
import com.mrl.pixiv.repository.IllustRepository
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import org.koin.core.annotation.Single

@Single
class BookmarkState(
    private val illustRepository: IllustRepository,
) : GlobalState<ImmutableMap<Long, Boolean>>(
    initialSate = persistentMapOf()
) {
    fun bookmarkIllust(
        illustId: Long,
        restrict: String = Restrict.PUBLIC,
        tags: List<String>? = null
    ) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.postIllustBookmarkAdd(
                    IllustBookmarkAddReq(illustId, restrict, tags)
                )
            ) {
                updateState {
                    it.toPersistentMap().put(illustId, true)
                }
            }
        }
    }

    fun deleteBookmarkIllust(illustId: Long) {
        launchNetwork {
            requestHttpDataWithFlow(
                request = illustRepository.postIllustBookmarkDelete(
                    IllustBookmarkDeleteReq(illustId)
                )
            ) {
                updateState {
                    it.toPersistentMap().put(illustId, false)
                }
            }
        }
    }
}