package com.mrl.pixiv.common.util

import com.mrl.pixiv.common.analytics.logException
import io.github.vinceglb.filekit.PlatformFile
import kotlinx.coroutines.CancellationException
import org.jetbrains.compose.resources.StringResource

suspend fun selectSaveFile(
    suggestedName: String,
    defaultExtension: String?,
    failureMessage: StringResource,
): PlatformFile? =
    try {
        platformSelectSaveFile(suggestedName, defaultExtension)
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (error: Exception) {
        logException(error)
        ToastUtil.safeShortToast(failureMessage, error.message.orEmpty())
        null
    }

internal expect suspend fun platformSelectSaveFile(
    suggestedName: String,
    defaultExtension: String?,
): PlatformFile?
