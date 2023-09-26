package com.mrl.pixiv.home.state

import androidx.compose.ui.unit.Dp
import com.mrl.pixiv.data.Illust

data class RecommendImageItemState(
    val id: Long,
    val thumbnail: String,
    val originImageUrl: String?,
    val originImageUrls: List<String>?,
    val title: String,
    val author: String,
    val width: Dp,
    val height: Dp,
    val totalView: Long,
    val totalBookmarks: Long,
    val isBookmarked: Boolean,
    val illust: Illust
) {
    override fun equals(other: Any?): Boolean {
        if (other is RecommendImageItemState) {
            return other.id == id && other.thumbnail == thumbnail && other.originImageUrl == originImageUrl
                    && other.originImageUrls == originImageUrls && other.title == title && other.author == author
                    && other.width == width && other.height == height && other.totalView == totalView
                    && other.totalBookmarks == totalBookmarks && other.isBookmarked == isBookmarked
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + thumbnail.hashCode()
        result = 31 * result + (originImageUrl?.hashCode() ?: 0)
        result = 31 * result + (originImageUrls?.hashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + author.hashCode()
        result = 31 * result + width.value.toInt()
        result = 31 * result + height.value.toInt()
        result = 31 * result + totalView.hashCode()
        result = 31 * result + totalBookmarks.hashCode()
        result = 31 * result + isBookmarked.hashCode()
        return result
    }
}

fun Illust.toRecommendImageItemState(recommendItemWidth: Dp): RecommendImageItemState {
    val scale = height * 1.0f / width
    return RecommendImageItemState(
        id = id,
        thumbnail = imageUrls.medium,
        originImageUrl = metaSinglePage.originalImageURL,
        originImageUrls = metaPages?.mapNotNull { it3 ->
            it3.imageUrls?.original
        },
        title = title,
        author = user.name,
        width = recommendItemWidth,
        height = recommendItemWidth * scale,
        totalView = totalView,
        totalBookmarks = totalBookmarks,
        isBookmarked = isBookmarked,
        illust = this
    )
}