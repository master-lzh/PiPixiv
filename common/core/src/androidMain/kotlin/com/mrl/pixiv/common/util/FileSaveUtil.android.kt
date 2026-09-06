package com.mrl.pixiv.common.util

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.openFileSaver

internal actual suspend fun platformSelectSaveFile(
    suggestedName: String,
    defaultExtension: String?,
): PlatformFile? = FileKit.openFileSaver(
    suggestedName = suggestedName,
    defaultExtension = defaultExtension,
)
