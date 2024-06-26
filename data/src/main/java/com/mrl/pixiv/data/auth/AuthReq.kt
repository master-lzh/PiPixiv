package com.mrl.pixiv.data.auth

import com.mrl.pixiv.data.Constants
import com.mrl.pixiv.data.IBaseFieldMap

data class AuthTokenFieldReq(
    val clientId: String = Constants.CLIENT_ID,
    val clientSecret: String = Constants.CLIENT_SECRET,
    val grantType: String,

    val username: String = "",
    val password: String = "",

    val code: String = "",
    val redirectUri: String = "",
    val codeVerifier: String = "",

    val refreshToken: String = "",

    val getSecureUrl: Int = 1,
    val includePolicy: Boolean = true,
) : IBaseFieldMap

enum class GrantType(val value: String) {
    PASSWORD("password"),
    REFRESH_TOKEN("refresh_token"),
    AUTHORIZATION_CODE("authorization_code")
}