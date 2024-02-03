package com.mrl.pixiv.common.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat

abstract class BaseActivity : ComponentActivity() {
    protected val TAG = this::class.java.simpleName

    @Composable
    abstract fun BuildContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent { BuildContent() }
    }
}