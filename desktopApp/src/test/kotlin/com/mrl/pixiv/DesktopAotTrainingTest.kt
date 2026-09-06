package com.mrl.pixiv

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private var aotSentinelInitialized = false

private object AotInitializationSentinel {
    init {
        aotSentinelInitialized = true
        error("AOT training must not initialize application objects")
    }

    fun example(value: String): List<String> =
        error("AOT training must not invoke methods: $value")
}

class DesktopAotTrainingTest {
    @Test
    fun normalStartupDoesNotEvaluateTrainingClassSelection() {
        for (mode in listOf(null, "", "off", "runtime")) {
            assertFalse(runDesktopAotTrainingIfRequested(mode) {
                error("Normal startup must not load training classes")
            })
        }
    }

    @Test
    fun trainingResolvesSignaturesWithoutInitializingOrInvokingObjects() {
        assertTrue(runDesktopAotTrainingIfRequested("training") {
            listOf(AotInitializationSentinel::class.java)
        })
        assertFalse(aotSentinelInitialized)
    }
}
