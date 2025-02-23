package com.mrl.pixiv.common.datasource.remote

import com.mrl.pixiv.common.data.EmptyResp
import com.mrl.pixiv.common.data.user.UserBookmarkTagsQuery
import com.mrl.pixiv.common.data.user.UserBookmarkTagsResp
import com.mrl.pixiv.common.data.user.UserBookmarksIllustQuery
import com.mrl.pixiv.common.data.user.UserBookmarksIllustResp
import com.mrl.pixiv.common.data.user.UserBookmarksNovelQuery
import com.mrl.pixiv.common.data.user.UserDetailQuery
import com.mrl.pixiv.common.data.user.UserDetailResp
import com.mrl.pixiv.common.data.user.UserFollowAddReq
import com.mrl.pixiv.common.data.user.UserFollowDeleteReq
import com.mrl.pixiv.common.data.user.UserFollowingQuery
import com.mrl.pixiv.common.data.user.UserFollowingResp
import com.mrl.pixiv.common.data.user.UserHistoryIllustsResp
import com.mrl.pixiv.common.data.user.UserIllustsQuery
import com.mrl.pixiv.common.data.user.UserIllustsResp
import com.mrl.pixiv.common.data.user.UserNovelsResp
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.http.parameters
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single

@Single
class UserHttpService(
    @Named("api") private val httpClient: HttpClient,
) {
    suspend fun getUserDetail(userDetailQuery: UserDetailQuery) =
        httpClient.safeGet<UserDetailResp>("/v2/user/detail") {
            userDetailQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun getUserIllusts(userIllustsQuery: UserIllustsQuery) =
        httpClient.safeGet<UserIllustsResp>("/v1/user/illusts") {
            userIllustsQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun getUserBookmarksIllust(userBookmarksIllustQuery: UserBookmarksIllustQuery) =
        httpClient.safeGet<UserBookmarksIllustResp>("/v1/user/bookmarks/illust") {
            userBookmarksIllustQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun loadMoreUserBookmarksIllust(map: Map<String, String>) =
        httpClient.safeGet<UserBookmarksIllustResp>("/v1/user/bookmarks/illust") {
            map.forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun getUserBookmarksNovels(userBookmarksNovelQuery: UserBookmarksNovelQuery) =
        httpClient.safeGet<UserNovelsResp>("/v1/user/bookmarks/novel") {
            userBookmarksNovelQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun followUser(userFollowAddReq: UserFollowAddReq) =
        httpClient.safePostForm<EmptyResp>(
            "/v1/user/follow/add",
            formParameters = parameters {
                userFollowAddReq.toMap().forEach { (key, value) ->
                    append(key, value)
                }
            }
        )

    suspend fun unFollowUser(userFollowDeleteReq: UserFollowDeleteReq) =
        httpClient.safePostForm<EmptyResp>(
            "/v1/user/follow/delete",
            formParameters = parameters {
                userFollowDeleteReq.toMap().forEach { (key, value) ->
                    append(key, value)
                }
            }
        )

    suspend fun getUserBrowsingHistoryIllusts() =
        httpClient.safeGet<UserHistoryIllustsResp>("/v1/user/browsing-history/illusts")

    suspend fun loadMoreUserBrowsingHistoryIllusts(map: Map<String, String>) =
        httpClient.safeGet<UserHistoryIllustsResp>("/v1/user/browsing-history/illusts") {
            map.forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun getUserBookmarkTagsIllust(userBookmarkTagsQuery: UserBookmarkTagsQuery) =
        httpClient.safeGet<UserBookmarkTagsResp>("/v1/user/bookmark-tags/illust") {
            userBookmarkTagsQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun getUserBookmarkTagsNovel(userBookmarkTagsQuery: UserBookmarkTagsQuery) =
        httpClient.safeGet<UserBookmarkTagsResp>("/v1/user/bookmark-tags/novel") {
            userBookmarkTagsQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }

    suspend fun getUserFollowing(userFollowingQuery: UserFollowingQuery) =
        httpClient.safeGet<UserFollowingResp>("/v1/user/following") {
            userFollowingQuery.toMap().forEach { (key, value) ->
                parameter(key, value)
            }
        }
}