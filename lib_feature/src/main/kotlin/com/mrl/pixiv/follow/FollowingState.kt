package com.mrl.pixiv.follow

import androidx.compose.runtime.Stable
import com.mrl.pixiv.common.data.Restrict

@Stable
data class FollowingState(
    val isSelf: Boolean = false,
    val restrict: String = Restrict.PUBLIC,
)
