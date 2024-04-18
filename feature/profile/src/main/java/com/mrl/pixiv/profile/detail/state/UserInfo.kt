package com.mrl.pixiv.profile.detail.state

import com.mrl.pixiv.data.User
import com.mrl.pixiv.data.user.UserDetailResp

data class UserInfo(
    val user: User? = null,
    val gender: Int = 0,
    val birth: String = "",
    val region: String = "",
    val totalFollowUsers: Long = 0,
    val totalMypixivUsers: Long = 0,
    val totalIllusts: Long = 0,
    val totalManga: Long = 0,
    val totalNovels: Long = 0,
    val totalIllustSeries: Long = 0,
    val totalNovelSeries: Long = 0,
    val backgroundImageURL: String = "",
    val twitterAccount: String = "",
    val twitterURL: String = "",
    val isPremium: Boolean = false,
    val isUsingCustomProfileImage: Boolean = false,
)

fun UserDetailResp.toUserInfo() = UserInfo(
    user = user,
    gender = profile.gender,
    birth = profile.birth,
    region = profile.region,
    totalFollowUsers = profile.totalFollowUsers,
    totalMypixivUsers = profile.totalMypixivUsers,
    totalIllusts = profile.totalIllusts,
    totalManga = profile.totalManga,
    totalNovels = profile.totalNovels,
    totalIllustSeries = profile.totalIllustSeries,
    totalNovelSeries = profile.totalNovelSeries,
    backgroundImageURL = profile.backgroundImageURL,
    twitterAccount = profile.twitterAccount,
    twitterURL = profile.twitterURL,
    isPremium = profile.isPremium,
    isUsingCustomProfileImage = profile.isUsingCustomProfileImage,
)