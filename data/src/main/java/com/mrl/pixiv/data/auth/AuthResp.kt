package com.mrl.pixiv.data.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenResp(
    @SerialName("access_token")
    val accessToken: String? = null,

    @SerialName("expires_in")
    val expiresIn: Long? = null,

    @SerialName("token_type")
    val tokenType: String? = null,

    val scope: String? = null,

    @SerialName("refresh_token")
    val refreshToken: String? = null,

    val user: AuthUser? = null,
    val response: AuthTokenResp? = null
)

@Serializable
data class AuthUser(
    @SerialName("profile_image_urls")
    val profileImageUrls: ProfileImageUrls,

    val id: String? = null,
    val name: String? = null,
    val account: String? = null,

    @SerialName("mail_address")
    val mailAddress: String? = null,

    @SerialName("is_premium")
    val isPremium: Boolean,

    @SerialName("x_restrict")
    val xRestrict: Long,

    @SerialName("is_mail_authorized")
    val isMailAuthorized: Boolean,

    @SerialName("require_policy_agreement")
    val requirePolicyAgreement: Boolean
)

@Serializable
data class ProfileImageUrls(
    @SerialName("px_16x16")
    val px16X16: String? = null,

    @SerialName("px_50x50")
    val px50X50: String? = null,

    @SerialName("px_170x170")
    val px170X170: String? = null
)
