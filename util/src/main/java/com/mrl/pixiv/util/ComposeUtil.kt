package com.mrl.pixiv.util

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role


const val VIEW_CLICK_INTERVAL_TIME = 1000L//点击间隔时间

inline fun Modifier.click(
    time: Long = VIEW_CLICK_INTERVAL_TIME,
    enabled: Boolean = true,//中间这三个是clickable自带的参数
    onClickLabel: String = "",
    role: Role? = null,
    crossinline onClick: () -> Unit
): Modifier = composed {
    composed {
        var lastClickTime by remember { mutableStateOf(value = 0L) }//使用remember函数记录上次点击的时间
        clickable(enabled, onClickLabel, role) {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - time >= lastClickTime) {//判断点击间隔,如果在间隔内则不回调
                onClick()
                lastClickTime = currentTimeMillis
            }
        }
    }
}

@Composable
inline fun composeClick(
    time: Long = VIEW_CLICK_INTERVAL_TIME,
    crossinline onClick: () -> Unit
): () -> Unit {
    //使用remember函数记录上次点击的时间
    var lastClickTime by remember { mutableStateOf(value = 0L) }
    return {
        val currentTimeMillis = System.currentTimeMillis()
        //判断点击间隔,如果在间隔内则不回调
        if (currentTimeMillis - time >= lastClickTime) {
            onClick()
            lastClickTime = currentTimeMillis
        }
    }
}