package com.mrl.pixiv.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Profile(
    val webpage: String? = null,
    val gender: Int? = null,
    val birth: String? = null,

    @SerialName("birth_day")
    val birthDay: String? = null,

    @SerialName("birth_year")
    val birthYear: Long? = null,

    val region: String? = null,

    @SerialName("address_id")
    val addressID: Long? = null,

    @SerialName("country_code")
    val countryCode: String? = null,

    val job: String? = null,

    @SerialName("job_id")
    val jobID: Long? = null,

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

    @SerialName("total_illust_series")
    val totalIllustSeries: Long,

    @SerialName("total_novel_series")
    val totalNovelSeries: Long,

    @SerialName("background_image_url")
    val backgroundImageURL: String? = null,

    @SerialName("twitter_account")
    val twitterAccount: String? = null,

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
    val gender: Long? = null,
    val region: Long? = null,

    @SerialName("birth_day")
    val birthDay: Long? = null,

    @SerialName("birth_year")
    val birthYear: Long? = null,

    val job: Long? = null,
    val pawoo: Boolean? = null
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