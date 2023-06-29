package com.mrl.pixiv.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mrl.pixiv.common.data.BaseUiState
import com.mrl.pixiv.home.state.RecommendImageItemState

class HomeUiState : BaseUiState() {
    var recommendImageList = mutableStateListOf<RecommendImageItemState>()
    var isRefresh: Boolean by mutableStateOf(false)
    var nextUrl: String by mutableStateOf("")
    var loadMore: Boolean by mutableStateOf(false)
    var refreshTokenResult: Boolean by mutableStateOf(false)
}