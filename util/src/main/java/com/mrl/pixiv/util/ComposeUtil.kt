package com.mrl.pixiv.util

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Indication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.semantics.Role


const val VIEW_CLICK_INTERVAL_TIME = 1000L//点击间隔时间

@SuppressLint("ModifierFactoryUnreferencedReceiver")
@OptIn(ExperimentalFoundationApi::class)
inline fun Modifier.throttleClick(
    time: Long = VIEW_CLICK_INTERVAL_TIME,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onLongClickLabel: String? = null,
    noinline onLongClick: (() -> Unit) = {},
    noinline onDoubleClick: (() -> Unit) = {},
    crossinline onClick: () -> Unit = {},
): Modifier = composed {
    var lastClickTime by remember { mutableLongStateOf(value = 0L) }//使用remember函数记录上次点击的时间
    val innerInteractionSource = remember { interactionSource ?: MutableInteractionSource() }
    combinedClickable(
        innerInteractionSource,
        indication,
        enabled,
        onClickLabel,
        role,
        onLongClickLabel,
        onLongClick,
        onDoubleClick
    ) {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - time >= lastClickTime) {//判断点击间隔,如果在间隔内则不回调
            onClick()
            lastClickTime = currentTimeMillis
        }
    }
}


@Composable
inline fun composeClick(
    time: Long = VIEW_CLICK_INTERVAL_TIME,
    crossinline onClick: () -> Unit
): () -> Unit {
    //使用remember函数记录上次点击的时间
    var lastClickTime by remember { mutableLongStateOf(value = 0L) }
    return {
        val currentTimeMillis = System.currentTimeMillis()
        //判断点击间隔,如果在间隔内则不回调
        if (currentTimeMillis - time >= lastClickTime) {
            onClick()
            lastClickTime = currentTimeMillis
        }
    }
}