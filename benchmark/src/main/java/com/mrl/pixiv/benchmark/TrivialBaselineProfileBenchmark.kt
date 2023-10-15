package com.mrl.pixiv.benchmark

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import org.junit.Rule
import org.junit.Test

class TrivialBaselineProfileBenchmark {
    // [START baseline_profile_basic]
    @RequiresApi(Build.VERSION_CODES.P)
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @RequiresApi(Build.VERSION_CODES.P)
    @Test
    fun startup() = baselineProfileRule.collect(
        packageName = "com.mrl.pixiv",
        profileBlock = {
            startActivityAndWait()
            device.waitForIdle()
        }
    )
    // [END baseline_profile_basic]
}