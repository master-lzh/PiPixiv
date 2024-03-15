package com.mrl.pixiv.common.lifecycle

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.mrl.pixiv.util.TAG

@Composable
fun OnLifecycle(
    lifecycleEvent: Lifecycle.Event = Lifecycle.Event.ON_CREATE,
    onLifecycle: () -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val onLifecycle by rememberUpdatedState(newValue = onLifecycle::invoke)
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == lifecycleEvent) {
                onLifecycle()
            }
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    Log.d(TAG, "$lifecycleOwner onCreate")
                }
                Lifecycle.Event.ON_START -> {
                    Log.d(TAG, "$lifecycleOwner On Start")
                }
                Lifecycle.Event.ON_RESUME -> {
                    Log.d(TAG, "$lifecycleOwner On Resume")
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG, "$lifecycleOwner On Pause")
                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d(TAG, "$lifecycleOwner On Stop")
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d(TAG, "$lifecycleOwner On Destroy")
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}