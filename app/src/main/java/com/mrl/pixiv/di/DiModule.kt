package com.mrl.pixiv.di

import com.mrl.pixiv.api.IllustApi
import com.mrl.pixiv.api.SearchApi
import com.mrl.pixiv.api.UserApi
import com.mrl.pixiv.api.UserAuthApi
import com.mrl.pixiv.common.coroutine.CloseableCoroutineScope
import com.mrl.pixiv.common.data.DispatcherEnum
import com.mrl.pixiv.common.middleware.auth.AuthMiddleware
import com.mrl.pixiv.common.middleware.auth.AuthReducer
import com.mrl.pixiv.common.middleware.bookmark.BookmarkMiddleware
import com.mrl.pixiv.common.middleware.bookmark.BookmarkReducer
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.middleware.follow.FollowMiddleware
import com.mrl.pixiv.common.middleware.follow.FollowReducer
import com.mrl.pixiv.common.middleware.follow.FollowViewModel
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.datasource.local.UserAuthDataSource
import com.mrl.pixiv.datasource.local.UserInfoDataSource
import com.mrl.pixiv.datasource.remote.IllustHttpService
import com.mrl.pixiv.datasource.remote.SearchHttpService
import com.mrl.pixiv.datasource.remote.UserAuthHttpService
import com.mrl.pixiv.datasource.remote.UserHttpService
import com.mrl.pixiv.domain.GetLocalUserInfoUseCase
import com.mrl.pixiv.domain.SetLocalUserInfoUseCase
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.domain.bookmark.BookmarkUseCase
import com.mrl.pixiv.domain.bookmark.UnBookmarkUseCase
import com.mrl.pixiv.home.viewmodel.HomeMiddleware
import com.mrl.pixiv.home.viewmodel.HomeReducer
import com.mrl.pixiv.home.viewmodel.HomeViewModel
import com.mrl.pixiv.login.viewmodel.LoginViewModel
import com.mrl.pixiv.network.HttpManager
import com.mrl.pixiv.network.converter.asConverterFactory
import com.mrl.pixiv.picture.viewmodel.PictureMiddleware
import com.mrl.pixiv.picture.viewmodel.PictureReducer
import com.mrl.pixiv.picture.viewmodel.PictureViewModel
import com.mrl.pixiv.profile.viewmodel.ProfileMiddleware
import com.mrl.pixiv.profile.viewmodel.ProfileReducer
import com.mrl.pixiv.profile.viewmodel.ProfileViewModel
import com.mrl.pixiv.repository.local.UserLocalRepository
import com.mrl.pixiv.repository.remote.AuthRemoteRepository
import com.mrl.pixiv.repository.remote.IllustRemoteRepository
import com.mrl.pixiv.repository.remote.SearchRemoteRepository
import com.mrl.pixiv.repository.remote.UserRemoteRepository
import com.mrl.pixiv.search.viewmodel.SearchMiddleware
import com.mrl.pixiv.search.viewmodel.SearchReducer
import com.mrl.pixiv.search.viewmodel.SearchViewModel
import com.mrl.pixiv.splash.viewmodel.SplashMiddleware
import com.mrl.pixiv.splash.viewmodel.SplashReducer
import com.mrl.pixiv.splash.viewmodel.SplashViewModel
import com.mrl.pixiv.userAuthDataStore
import com.mrl.pixiv.userInfoDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
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

    single(named(DispatcherEnum.MAIN)) { Dispatchers.Main.immediate }

    single { JSON }

    factory {
        CloseableCoroutineScope(SupervisorJob() + get<MainCoroutineDispatcher>(named(DispatcherEnum.MAIN)))
    }
}

val viewModelModule = module {
    viewModel { SplashViewModel(get(), get()) }

    viewModel { LoginViewModel(get(), get()) }

    viewModel { HomeViewModel(get(), get()) }

    viewModel { ProfileViewModel(get(), get()) }

    viewModel { (illust: Illust) -> PictureViewModel(illust, get(), get()) }

    viewModel { BookmarkViewModel(get(), get()) }

    viewModel { FollowViewModel(get(), get()) }

    viewModel { SearchViewModel(get(), get()) }
}

val repositoryModule = module {
    single { UserLocalRepository(get(), get()) }


    single { AuthRemoteRepository(get(), get(named(DispatcherEnum.IO))) }
    single { IllustRemoteRepository(get(), get(named(DispatcherEnum.IO))) }
    single { UserRemoteRepository(get(), get(named(DispatcherEnum.IO))) }
    single { SearchRemoteRepository(get(), get(named(DispatcherEnum.IO))) }
}

val dataSourceModule = module {
    single { UserAuthDataSource(get(named(DataStoreEnum.USER_AUTH))) }
    single { UserInfoDataSource(get(named(DataStoreEnum.USER_INFO))) }

    single { IllustHttpService(provideCommonService(get(), IllustApi::class.java)) }
    single { UserAuthHttpService(provideAuthService(get())) }
    single { UserHttpService(provideCommonService(get(), UserApi::class.java)) }
    single { SearchHttpService(provideCommonService(get(), SearchApi::class.java)) }
}

val useCaseModule = module {
    single { SetUserRefreshTokenUseCase(get()) }
    single { SetUserAccessTokenUseCase(get()) }
    single { GetLocalUserInfoUseCase(get()) }
    single { SetLocalUserInfoUseCase(get()) }
    single { RefreshUserAccessTokenUseCase(get(), get(), get(), get(), get()) }
    single { BookmarkUseCase(get()) }
    single { UnBookmarkUseCase(get()) }
}

val middlewareModule = module {
    factory { SplashMiddleware(get(), get(), get(), get(), get()) }

    factory { HomeMiddleware(get(), get()) }

    factory { BookmarkMiddleware(get()) }

    factory { AuthMiddleware(get(), get(), get(), get(), get(), get()) }

    factory { ProfileMiddleware(get(), get()) }

    factory { PictureMiddleware(get(), get()) }

    factory { FollowMiddleware(get()) }

    factory { SearchMiddleware(get()) }
}

val reducerModule = module {
    single { SplashReducer() }
    single { HomeReducer() }
    single { BookmarkReducer() }
    single { AuthReducer() }
    single { ProfileReducer() }
    single { PictureReducer() }
    single { FollowReducer() }
    single { SearchReducer() }
}

fun provideAuthService(
    httpManager: HttpManager,
): UserAuthApi = httpManager.getAuthService(UserAuthApi::class.java)

fun <T> provideCommonService(
    httpManager: HttpManager,
    clazz: Class<T>,
): T = httpManager.getCommonService(clazz)

