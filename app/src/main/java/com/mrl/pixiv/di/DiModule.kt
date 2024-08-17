package com.mrl.pixiv.di

import com.mrl.pixiv.collection.viewmodel.CollectionMiddleware
import com.mrl.pixiv.collection.viewmodel.CollectionReducer
import com.mrl.pixiv.collection.viewmodel.CollectionViewModel
import com.mrl.pixiv.common.middleware.auth.AuthMiddleware
import com.mrl.pixiv.common.middleware.auth.AuthReducer
import com.mrl.pixiv.common.middleware.bookmark.BookmarkMiddleware
import com.mrl.pixiv.common.middleware.bookmark.BookmarkReducer
import com.mrl.pixiv.common.middleware.bookmark.BookmarkViewModel
import com.mrl.pixiv.common.middleware.follow.FollowMiddleware
import com.mrl.pixiv.common.middleware.follow.FollowReducer
import com.mrl.pixiv.common.middleware.follow.FollowViewModel
import com.mrl.pixiv.common.middleware.illust.IllustMiddleware
import com.mrl.pixiv.common.middleware.illust.IllustReducer
import com.mrl.pixiv.common.middleware.illust.IllustViewModel
import com.mrl.pixiv.datasource.DatasourceModule
import com.mrl.pixiv.domain.GetLocalUserInfoUseCase
import com.mrl.pixiv.domain.HasShowBookmarkTipUseCase
import com.mrl.pixiv.domain.SetLocalUserInfoUseCase
import com.mrl.pixiv.domain.SetShowBookmarkTipUseCase
import com.mrl.pixiv.domain.SetUserAccessTokenUseCase
import com.mrl.pixiv.domain.SetUserRefreshTokenUseCase
import com.mrl.pixiv.domain.auth.RefreshUserAccessTokenUseCase
import com.mrl.pixiv.domain.illust.GetIllustBookmarkDetailUseCase
import com.mrl.pixiv.domain.setting.GetAppThemeUseCase
import com.mrl.pixiv.history.viewmodel.HistoryMiddleware
import com.mrl.pixiv.history.viewmodel.HistoryReducer
import com.mrl.pixiv.history.viewmodel.HistoryViewModel
import com.mrl.pixiv.home.viewmodel.HomeMiddleware
import com.mrl.pixiv.home.viewmodel.HomeReducer
import com.mrl.pixiv.home.viewmodel.HomeViewModel
import com.mrl.pixiv.login.viewmodel.LoginViewModel
import com.mrl.pixiv.picture.viewmodel.PictureDeeplinkViewModel
import com.mrl.pixiv.picture.viewmodel.PictureMiddleware
import com.mrl.pixiv.picture.viewmodel.PictureReducer
import com.mrl.pixiv.picture.viewmodel.PictureViewModel
import com.mrl.pixiv.profile.detail.viewmodel.ProfileDetailMiddleware
import com.mrl.pixiv.profile.detail.viewmodel.ProfileDetailReducer
import com.mrl.pixiv.profile.detail.viewmodel.ProfileDetailViewModel
import com.mrl.pixiv.profile.viewmodel.ProfileMiddleware
import com.mrl.pixiv.profile.viewmodel.ProfileReducer
import com.mrl.pixiv.profile.viewmodel.ProfileViewModel
import com.mrl.pixiv.repository.AuthRepository
import com.mrl.pixiv.repository.CollectionRepository
import com.mrl.pixiv.repository.HistoryRepository
import com.mrl.pixiv.repository.IllustRepository
import com.mrl.pixiv.repository.SearchRepository
import com.mrl.pixiv.repository.SettingRepository
import com.mrl.pixiv.repository.TrendingRepository
import com.mrl.pixiv.repository.UserRepository
import com.mrl.pixiv.search.preview.viewmodel.SearchPreviewMiddleware
import com.mrl.pixiv.search.preview.viewmodel.SearchPreviewReducer
import com.mrl.pixiv.search.preview.viewmodel.SearchPreviewViewModel
import com.mrl.pixiv.search.viewmodel.SearchMiddleware
import com.mrl.pixiv.search.viewmodel.SearchReducer
import com.mrl.pixiv.search.viewmodel.SearchViewModel
import com.mrl.pixiv.setting.viewmodel.SettingMiddleware
import com.mrl.pixiv.setting.viewmodel.SettingReducer
import com.mrl.pixiv.setting.viewmodel.SettingViewModel
import com.mrl.pixiv.splash.viewmodel.SplashMiddleware
import com.mrl.pixiv.splash.viewmodel.SplashReducer
import com.mrl.pixiv.splash.viewmodel.SplashViewModel
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import org.koin.ksp.generated.module


val JSON = Json {
    ignoreUnknownKeys = true
    coerceInputValues = true
    isLenient = true
}

val appModule = module {
    singleOf(::JSON)
}

val viewModelModule = module {
    viewModelOf(::SplashViewModel)

    viewModelOf(::LoginViewModel)

    viewModelOf(::HomeViewModel)

    viewModelOf(::ProfileViewModel)

    viewModelOf(::ProfileDetailViewModel)

    viewModelOf(::PictureViewModel)

    viewModelOf(::PictureDeeplinkViewModel)

    viewModelOf(::BookmarkViewModel)

    viewModelOf(::FollowViewModel)

    viewModelOf(::SearchViewModel)

    viewModelOf(::SearchPreviewViewModel)

    viewModelOf(::SettingViewModel)

    viewModelOf(::HistoryViewModel)

    viewModelOf(::CollectionViewModel)

    viewModelOf(::IllustViewModel)
}

val repositoryModule = module {
    singleOf(::UserRepository)
    singleOf(::SettingRepository)


    singleOf(::AuthRepository)
    singleOf(::IllustRepository)
    singleOf(::SearchRepository)
    singleOf(::TrendingRepository)
    singleOf(::HistoryRepository)
    singleOf(::CollectionRepository)
}

val useCaseModule = module {
    singleOf(::SetUserRefreshTokenUseCase)
    singleOf(::SetUserAccessTokenUseCase)
    singleOf(::GetLocalUserInfoUseCase)
    singleOf(::SetLocalUserInfoUseCase)
    singleOf(::RefreshUserAccessTokenUseCase)
    singleOf(::GetAppThemeUseCase)
    singleOf(::GetIllustBookmarkDetailUseCase)
    singleOf(::HasShowBookmarkTipUseCase)
    singleOf(::SetShowBookmarkTipUseCase)
}

val middlewareModule = module {
    factoryOf(::SplashMiddleware)

    factoryOf(::HomeMiddleware)

    factoryOf(::BookmarkMiddleware)

    factoryOf(::AuthMiddleware)

    factoryOf(::ProfileMiddleware)

    factoryOf(::ProfileDetailMiddleware)

    factoryOf(::PictureMiddleware)

    factoryOf(::FollowMiddleware)

    factoryOf(::SearchMiddleware)

    factoryOf(::SearchPreviewMiddleware)

    factoryOf(::SettingMiddleware)

    factoryOf(::HistoryMiddleware)

    factoryOf(::CollectionMiddleware)

    factoryOf(::IllustMiddleware)
}

val reducerModule = module {
    singleOf(::SplashReducer)
    singleOf(::HomeReducer)
    singleOf(::BookmarkReducer)
    singleOf(::AuthReducer)
    singleOf(::ProfileReducer)
    singleOf(::ProfileDetailReducer)
    singleOf(::PictureReducer)
    singleOf(::FollowReducer)
    singleOf(::SearchReducer)
    singleOf(::SearchPreviewReducer)
    singleOf(::SettingReducer)
    singleOf(::HistoryReducer)
    singleOf(::CollectionReducer)
    singleOf(::IllustReducer)
}

val allModule = listOf(
    defaultModule,
    appModule,
    viewModelModule,
    repositoryModule,
    DatasourceModule.module,
    useCaseModule,
    middlewareModule,
    reducerModule,
)
