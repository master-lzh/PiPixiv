package com.mrl.pixiv

import androidx.compose.runtime.Composition
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import com.mrl.pixiv.common.repository.SettingRepository
import com.mrl.pixiv.common.util.AppUtil
import com.mrl.pixiv.di.Initialization
import dev.nucleusframework.window.tao.TaoApplication

/** Returns true when main should return before initializing any application services. */
internal fun runDesktopAotTrainingIfRequested(
    mode: String? = System.getProperty("nucleus.aot.mode"),
    trainingClasses: () -> List<Class<*>> = ::desktopAotTrainingClasses,
): Boolean {
    if (mode != "training") return false

    val classes = trainingClasses()
    // Resolving signatures does not invoke methods, constructors or read static fields.
    // In particular, never access a Kotlin object's INSTANCE during this pass.
    val members = classes.sumOf { type ->
        type.declaredMethods.size + type.declaredConstructors.size + type.declaredFields.size
    }
    println("DESKTOP_AOT_TRAINING completed classes=${classes.size} members=$members")
    return true
}

private fun desktopAotTrainingClasses(): List<Class<*>> = listOf(
    // Class literals survive ProGuard renaming without initializing these classes or objects.
    State::class.java,
    Composition::class.java,
    Modifier::class.java,
    Density::class.java,
    TaoApplication::class.java,
    Initialization::class.java,
    AppUtil::class.java,
    SettingRepository::class.java,
)
