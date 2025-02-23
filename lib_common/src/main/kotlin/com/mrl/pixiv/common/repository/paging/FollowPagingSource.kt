package com.mrl.pixiv.common.repository.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mrl.pixiv.common.data.Filter
import com.mrl.pixiv.common.data.Restrict
import com.mrl.pixiv.common.data.Rlt
import com.mrl.pixiv.common.data.user.UserFollowingQuery
import com.mrl.pixiv.common.data.user.UserFollowingResp
import com.mrl.pixiv.common.repository.UserFollowingRepository
import com.mrl.pixiv.common.util.queryParams
import kotlinx.coroutines.flow.first

class FollowPagingSource(
    private val userId: Long,
    private val userFollowingRepository: UserFollowingRepository,
) : PagingSource<UserFollowingQuery, UserFollowingResp.UserPreview>() {
    override fun getRefreshKey(state: PagingState<UserFollowingQuery, UserFollowingResp.UserPreview>): UserFollowingQuery? {
        return null
    }

    override suspend fun load(params: LoadParams<UserFollowingQuery>): LoadResult<UserFollowingQuery, UserFollowingResp.UserPreview> {
        val query = if (params.key == null) {
            UserFollowingQuery(
                userId = userId,
            )
        } else {
            params.key!!
        }
        val respFlow = userFollowingRepository.getUserFollowing(query)
        return when (val resp = respFlow.first()) {
            is Rlt.Success -> {
                val nextQuery = resp.data.nextUrl?.queryParams
                val nextKey = UserFollowingQuery(
                    filter = Filter.entries.find { it.value == nextQuery?.get("filter") }
                        ?: Filter.ANDROID,
                    userId = userId,
                    restrict = nextQuery?.get("restrict") ?: Restrict.PUBLIC,
                    offset = nextQuery?.get("offset")?.toIntOrNull(),
                )
                LoadResult.Page(
                    data = resp.data.userPreviews,
                    prevKey = params.key,
                    nextKey = nextKey
                )
            }

            is Rlt.Failed -> {
                LoadResult.Error(resp.error.exception)
            }
        }
    }
}