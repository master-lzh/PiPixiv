package com.mrl.pixiv.data.user

import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.data.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserIllustsResp(
    val user: User,
    val illusts: List<Illust>,

    @SerialName("next_url")
    val nextURL: String
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
    val nextURL: String
)

