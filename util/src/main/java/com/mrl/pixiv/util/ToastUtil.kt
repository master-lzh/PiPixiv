package com.mrl.pixiv.util

import android.widget.Toast
import androidx.annotation.StringRes

object ToastUtil {
    fun shortToast(@StringRes strId: Int) {
        val text = AppUtil.appContext.getString(strId)
        Toast.makeText(AppUtil.appContext, text, Toast.LENGTH_SHORT).show()
    }

    fun longToast(@StringRes strId: Int) {
        val text = AppUtil.appContext.getString(strId)
        Toast.makeText(AppUtil.appContext, text, Toast.LENGTH_LONG).show()
    }
}