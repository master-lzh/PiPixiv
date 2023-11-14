package com.mrl.pixiv.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement


@Serializable
class EmptyResp

@Serializable
data class ProfileImageUrls(
    val medium: String = ""
)

@Serializable
data class ImageUrls(
    @SerialName("square_medium")
    val squareMedium: String = "",

    val medium: String = "",
    val large: String = "",
    val original: String = ""
)

@Serializable
data class Illust(
    val id: Long,
    val title: String,
    val type: Type,

    @SerialName("image_urls")
    val imageUrls: ImageUrls,

    val caption: String = "",
    val restrict: Long = 0,
    val user: User,
    val tags: List<Tag>? = null,
    val tools: List<String>? = null,

    @SerialName("create_date")
    val createDate: String = "",

    @SerialName("page_count")
    val pageCount: Int = 0,

    val width: Int,
    val height: Int,

    @SerialName("sanity_level")
    val sanityLevel: Int = 0,

    @SerialName("x_restrict")
    val xRestrict: Int = 0,

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
    val illustAIType: Int = 0,

    @SerialName("illust_book_style")
    val illustBookStyle: Int = 0
)

@Serializable
data class MetaPage(
    @SerialName("image_urls")
    val imageUrls: ImageUrls? = null
)

@Serializable
data class MetaSinglePage(
    @SerialName("original_image_url")
    val originalImageURL: String = ""
)

@Serializable
data class Tag(
    val name: String = "",

    @SerialName("translated_name")
    val translatedName: String = "",

    @SerialName("added_by_uploaded_user")
    val addedByUploadedUser: Boolean = false
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
    val isFollowed: Boolean = false,

    @SerialName("is_access_blocking_user")
    val isAccessBlockingUser: Boolean = false
)

@Serializable
data class Novel (
    val id: Long,
    val title: String,
    val caption: String,
    val restrict: Long,

    @SerialName("x_restrict")
    val xRestrict: Long,

    @SerialName("is_original")
    val isOriginal: Boolean,

    @SerialName("image_urls")
    val imageUrls: ImageUrls,

    @SerialName("create_date")
    val createDate: String,

    val tags: List<Tag>,

    @SerialName("page_count")
    val pageCount: Long,

    @SerialName("text_length")
    val textLength: Long,

    val user: User,
    val series: Series,

    @SerialName("is_bookmarked")
    val isBookmarked: Boolean,

    @SerialName("total_bookmarks")
    val totalBookmarks: Long,

    @SerialName("total_view")
    val totalView: Long,

    val visible: Boolean,

    @SerialName("total_comments")
    val totalComments: Long,

    @SerialName("is_muted")
    val isMuted: Boolean,

    @SerialName("is_mypixiv_only")
    val isMypixivOnly: Boolean,

    @SerialName("is_x_restricted")
    val isXRestricted: Boolean,

    @SerialName("novel_ai_type")
    val novelAiType: Long
)


@Serializable
data class Series (
    val id: Long? = null,
    val title: String? = null
)