package com.mrl.pixiv.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


@Serializable
class EmptyResp

@Serializable
data class ProfileImageUrls(
    val medium: String? = null
)

@Serializable
data class ImageUrls(
    @SerialName("square_medium")
    val squareMedium: String? = null,

    val medium: String? = null,
    val large: String? = null,
    val original: String? = null
)

@Serializable
data class Illust(
    val id: Long,
    val title: String,
    val type: Type,

    @SerialName("image_urls")
    val imageUrls: ImageUrls,

    val caption: String? = null,
    val restrict: Long? = null,
    val user: User,
    val tags: List<Tag>? = null,
    val tools: List<String>? = null,

    @SerialName("create_date")
    val createDate: String? = null,

    @SerialName("page_count")
    val pageCount: Int? = null,

    val width: Int,
    val height: Int,

    @SerialName("sanity_level")
    val sanityLevel: Int? = null,

    @SerialName("x_restrict")
    val xRestrict: Int? = null,

    val series: JsonElement? = null,

    @SerialName("meta_single_page")
    val metaSinglePage: MetaSinglePage,

    @SerialName("meta_pages")
    val metaPages: List<MetaPage>? = null,

    @SerialName("total_view")
    val totalView: Long,

    @SerialName("total_bookmarks")
    val totalBookmarks: Long,

    @SerialName("is_bookmarked")
    val isBookmarked: Boolean,

    val visible: Boolean? = null,

    @SerialName("is_muted")
    val isMuted: Boolean? = null,

    @SerialName("illust_ai_type")
    val illustAIType: Int? = null,

    @SerialName("illust_book_style")
    val illustBookStyle: Int? = null
)

@Serializable
data class MetaPage(
    @SerialName("image_urls")
    val imageUrls: ImageUrls? = null
)

@Serializable
data class MetaSinglePage(
    @SerialName("original_image_url")
    val originalImageURL: String? = null
)

@Serializable
data class Tag(
    val name: String? = null,

    @SerialName("translated_name")
    val translatedName: String? = null
)

@Serializable
enum class Type(val value: String) {
    @SerialName("illust")
    Illust("illust"),

    @SerialName("manga")
    Manga("manga"),

    @SerialName("ugoira")
    Ugoira("ugoira");
}

@Serializable
data class User(
    val id: Long,
    val name: String,
    val account: String,

    @SerialName("profile_image_urls")
    val profileImageUrls: ProfileImageUrls,

    val comment: String = "",

    @SerialName("is_followed")
    val isFollowed: Boolean,

    @SerialName("is_access_blocking_user")
    val isAccessBlockingUser: Boolean = false
)