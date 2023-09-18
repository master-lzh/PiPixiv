package com.mrl.pixiv.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Profile(
    val webpage: String = "",
    val gender: Int = 0,
    val birth: String = "",

    @SerialName("birth_day")
    val birthDay: String = "",

    @SerialName("birth_year")
    val birthYear: Long = 0,

    val region: String = "",

    @SerialName("address_id")
    val addressID: Long = 0,

    @SerialName("country_code")
    val countryCode: String = "",

    val job: String = "",

    @SerialName("job_id")
    val jobID: Long = 0,

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
    val backgroundImageURL: String = "",

    @SerialName("twitter_account")
    val twitterAccount: String = "",

    @SerialName("twitter_url")
    val twitterURL: String = "",

    @SerialName("pawoo_url")
    val pawooURL: String = "",

    @SerialName("is_premium")
    val isPremium: Boolean,

    @SerialName("is_using_custom_profile_image")
    val isUsingCustomProfileImage: Boolean
)

@Serializable
data class ProfilePublicity(
    val gender: Long = 0,
    val region: Long = 0,

    @SerialName("birth_day")
    val birthDay: Long = 0,

    @SerialName("birth_year")
    val birthYear: Long = 0,

    val job: Long = 0,
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
    val workspaceImageURL: String = ""
)