package com.mrl.pixiv.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mrl.pixiv.common.data.BaseUiState
import com.mrl.pixiv.profile.state.UserInfo

class ProfileUiState : BaseUiState() {
    var userInfo by mutableStateOf(UserInfo())
}