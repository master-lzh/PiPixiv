package com.mrl.pixiv.picture

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mrl.pixiv.common.data.BaseUiState
import com.mrl.pixiv.data.Illust

class PictureUiState : BaseUiState() {
    var illustRelated = mutableStateListOf<Illust>()
    var userIllusts = mutableStateListOf<Illust>()
}