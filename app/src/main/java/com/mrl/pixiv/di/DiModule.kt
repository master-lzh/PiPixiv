package com.mrl.pixiv.di

import com.mrl.pixiv.api.IllustApi
import com.mrl.pixiv.api.UserApi
import com.mrl.pixiv.api.UserAuthApi
import com.mrl.pixiv.common.data.DispatcherEnum
import com.mrl.pixiv.common_viewmodel.bookmark.BookmarkViewModel
import com.mrl.pixiv.datasource.local.UserAuthDataSource
import com.mrl.pixiv.datasource.local.UserInfoDataSource
import com.mrl.pixiv.datasource.remote.IllustHttpService
import com.mrl.pixiv.datasource.remote.UserAuthHttpService
import com.mrl.pixiv.datasource.remote.UserHttpService
import com.mrl.pixiv.domain.GetUserIdUseCase
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserIdUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.home.HomeViewModel
import com.mrl.pixiv.login.LoginViewModel
import com.mrl.pixiv.network.HttpManager
import com.mrl.pixiv.network.converter.asConverterFactory
import com.mrl.pixiv.picture.PictureViewModel
import com.mrl.pixiv.profile.ProfileViewModel
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.AuthRemoteRepository
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository
import com.mrl.pixiv.splash.SplashViewModel
import com.mrl.pixiv.userAuthDataStore
import com.mrl.pixiv.userInfoDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

enum class DataStoreEnum {
    USER_AUTH,
    USER_INFO,
}


val JSON = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}

val appModule = module {
    single(named(DataStoreEnum.USER_AUTH)) { androidContext().userAuthDataStore }

    single(named(DataStoreEnum.USER_INFO)) { androidContext().userInfoDataStore }

    single {
        JSON.asConverterFactory("application/json".toMediaType())
    }

    single { HttpManager(get(), get()) }

    single(named(DispatcherEnum.IO)) { Dispatchers.IO }
}

val viewModelModule = module {
    viewModel { SplashViewModel(get(), get(), get(), get()) }

    viewModel { LoginViewModel(get(), get(), get(), get()) }

    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }

    viewModel { ProfileViewModel(get(), get()) }

    viewModel { PictureViewModel(get(), get()) }

    viewModel { BookmarkViewModel(get(), get()) }
}

val repositoryModule = module {
    single { UserLocalRepository(get(), get()) }


    single { AuthRemoteRepository(get(), get(named(DispatcherEnum.IO))) }
    single { IllustRemoteRepository(get(), get(named(DispatcherEnum.IO))) }
    single { UserRemoteRepository(get(), get(named(DispatcherEnum.IO))) }
}

val dataSourceModule = module {
    single { UserAuthDataSource(get(named(DataStoreEnum.USER_AUTH))) }
    single { UserInfoDataSource(get(named(DataStoreEnum.USER_INFO))) }

    single { IllustHttpService(provideIllustService(get())) }
    single { UserAuthHttpService(provideAuthService(get())) }
    single { UserHttpService(provideUserService(get())) }
}

val useCaseModule = module {
    single { SetUserRefreshTokenUseCase(get()) }
    single { SetUserAccessTokenUseCase(get()) }
    single { GetUserIdUseCase(get()) }
    single { SetUserIdUseCase(get()) }
}

fun provideAuthService(
    httpManager: HttpManager,
): UserAuthApi = httpManager.getAuthService(UserAuthApi::class.java)

fun provideUserService(
    httpManager: HttpManager,
): UserApi = httpManager.getCommonService(UserApi::class.java)

fun provideIllustService(
    httpManager: HttpManager,
): IllustApi = httpManager.getCommonService(IllustApi::class.java)

