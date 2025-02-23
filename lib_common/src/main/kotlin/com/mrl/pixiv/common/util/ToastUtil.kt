package com.mrl.pixiv.common.util

import android.widget.Toast
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

object ToastUtil : CoroutineScope by MainScope() {
    fun safeShortToast(@StringRes strId: Int) {
        launch {
            val text = AppUtil.appContext.getString(strId)
            Toast.makeText(AppUtil.appContext, text, Toast.LENGTH_SHORT).show()
        }
    }

    fun safeLongToast(@StringRes strId: Int) {
        launch {
            val text = AppUtil.appContext.getString(strId)
            Toast.makeText(AppUtil.appContext, text, Toast.LENGTH_LONG).show()
        }
    }
}