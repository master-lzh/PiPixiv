package com.mrl.pixiv.home.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mrl.pixiv.data.Illust

class RecommendImageItemState {
    var id: Long by mutableLongStateOf(0)
    var thumbnail: String by mutableStateOf("")
    var originImageUrl: String? by mutableStateOf(null)
    var originImageUrls: List<String>? by mutableStateOf(null)
    var title: String by mutableStateOf("")
    var author: String by mutableStateOf("")
    var width: Int by mutableIntStateOf(0)
    var height: Int by mutableIntStateOf(0)
    var totalView: Long by mutableLongStateOf(0)
    var totalBookmarks: Long by mutableLongStateOf(0)
    var isBookmarked: Boolean by mutableStateOf(false)
    lateinit var illust: Illust


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
        result = 31 * result + width
        result = 31 * result + height
        result = 31 * result + totalView.hashCode()
        result = 31 * result + totalBookmarks.hashCode()
        result = 31 * result + isBookmarked.hashCode()
        return result
    }
}