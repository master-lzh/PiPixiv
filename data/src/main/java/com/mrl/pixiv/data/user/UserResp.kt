package com.mrl.pixiv.data.user

import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.Novel
import com.mrl.pixiv.data.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class UserIllustsResp(
    val user: User? = null,
    val illusts: List<Illust>,

    @SerialName("next_url")
    val nextURL: String = ""
)

@Serializable
data class UserDetailResp(
    val user: User,
    val profile: Profile,

    @SerialName("profile_publicity")
    val profilePublicity: ProfilePublicity,

    val workspace: Workspace
)

@Serializable
data class UserBookmarksIllustResp(
    val illusts: List<Illust>,

    @SerialName("next_url")
    val nextURL: String?
)

@Serializable
data class UserNovelsResp (
    val novels: List<Novel>,

    @SerialName("next_url")
    val nextUrl: JsonElement? = null
)

@Serializable
data class UserHistoryIllustsResp(
    val illusts: List<Illust>,

    @SerialName("next_url")
    val nextURL: String? = null
)

@Serializable
data class UserBookmarkTagsResp (
    @SerialName("bookmark_tags")
    val bookmarkTags: List<BookmarkTag>,

    @SerialName("next_url")
    val nextUrl: String? = null
)

