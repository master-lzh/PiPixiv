package com.mrl.pixiv.common.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import com.mrl.pixiv.common.coroutine.launchIO
import com.mrl.pixiv.common.data.setting.UserPreference
import com.mrl.pixiv.common.data.user.UserInfo
import com.mrl.pixiv.common.repository.SettingRepository
import com.mrl.pixiv.common.repository.UserRepository
import kotlinx.coroutines.flow.first
import org.koin.compose.LocalKoinApplication
import org.koin.core.annotation.Single

data class GlobalInfo(
    val userPreference: UserPreference,
    val userInfo: UserInfo
)

@Single
class GlobalStore(
    private val userRepository: UserRepository,
    private val settingRepository: SettingRepository
) : GlobalState<GlobalInfo>(
    initialSate = GlobalInfo(
        userPreference = UserPreference.defaultInstance,
        userInfo = UserInfo.defaultInstance
    )
) {
    init {
        launchIO {
            userRepository.userInfo.first().let { userInfo ->
                updateState { it.copy(userInfo = userInfo) }
            }
            settingRepository.allSettings.first().let { allSettings ->
                updateState { it.copy(userPreference = allSettings) }
            }
        }
        launchIO {
            userRepository.userInfo.collect { userInfo ->
                updateState { it.copy(userInfo = userInfo) }
            }
        }
        launchIO {
            settingRepository.allSettings.collect { allSettings ->
                updateState { it.copy(userPreference = allSettings) }
            }
        }
    }
}

@Composable
fun globalStore(): GlobalInfo {
    val koin = LocalKoinApplication.current
    val globalStore = koin.get<GlobalStore>()
    return globalStore.state
}

val LocalGlobalStore = compositionLocalOf<GlobalInfo> {
    error("No GlobalStore provided")
}
