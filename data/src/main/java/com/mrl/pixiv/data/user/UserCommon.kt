package com.mrl.pixiv.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Profile(
    val webpage: String? = null,
    val gender: String,
    val birth: String,

    @SerialName("birth_day")
    val birthDay: String,

    @SerialName("birth_year")
    val birthYear: Long,

    val region: String,

    @SerialName("address_id")
    val addressID: Long,

    @SerialName("country_code")
    val countryCode: String,

    val job: String,

    @SerialName("job_id")
    val jobID: Long,

    @SerialName("total_follow_users")
    val totalFollowUsers: Long,

    @SerialName("total_mypixiv_users")
    val totalMypixivUsers: Long,

    @SerialName("total_illusts")
    val totalIllusts: Long,

    @SerialName("total_manga")
    val totalManga: Long,

    @SerialName("total_novels")
    val totalNovels: Long,

    @SerialName("total_illust_bookmarks_public")
    val totalIllustBookmarksPublic: Long,

    @SerialName("total_illust_series")
    val totalIllustSeries: Long,

    @SerialName("total_novel_series")
    val totalNovelSeries: Long,

    @SerialName("background_image_url")
    val backgroundImageURL: String? = null,

    @SerialName("twitter_account")
    val twitterAccount: String,

    @SerialName("twitter_url")
    val twitterURL: String? = null,

    @SerialName("pawoo_url")
    val pawooURL: String? = null,

    @SerialName("is_premium")
    val isPremium: Boolean,

    @SerialName("is_using_custom_profile_image")
    val isUsingCustomProfileImage: Boolean
)

@Serializable
data class ProfilePublicity(
    val gender: String,
    val region: String,

    @SerialName("birth_day")
    val birthDay: String,

    @SerialName("birth_year")
    val birthYear: String,

    val job: String,
    val pawoo: Boolean
)

@Serializable
data class Workspace(
    val pc: String,
    val monitor: String,
    val tool: String,
    val scanner: String,
    val tablet: String,
    val mouse: String,
    val printer: String,
    val desktop: String,
    val music: String,
    val desk: String,
    val chair: String,
    val comment: String,

    @SerialName("workspace_image_url")
    val workspaceImageURL: String? = null
)