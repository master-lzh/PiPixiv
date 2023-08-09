package com.mrl.pixiv.network

import java.io.IOException

class HttpException(val code: Int, override val message: String, throwable: Throwable?) :
    IOException(message, throwable) {
    companion object {
        const val CODE_UNKNOWN = -1
        const val CODE_NETWORK = -2
        const val CODE_SERVER = -3
        const val CODE_PARSE = -4
        const val CODE_TIMEOUT = -5
        const val CODE_TOKEN_EXPIRED = 401
        const val CODE_TOKEN_INVALID = 403
        const val CODE_TOKEN_NOT_FOUND = 404
        // 500
        const val CODE_SERVER_ERROR = 500
        // 502
        const val CODE_BAD_GATEWAY = 502
    }
}