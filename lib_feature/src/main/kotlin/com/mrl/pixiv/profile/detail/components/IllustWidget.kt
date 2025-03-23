package com.mrl.pixiv.profile.detail.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrl.pixiv.common.data.Illust
import com.mrl.pixiv.common.ui.item.SquareIllustItem
import com.mrl.pixiv.common.viewmodel.bookmark.BookmarkState
import com.mrl.pixiv.common.viewmodel.bookmark.requireBookmarkState

private const val SPAN_COUNT = 3
private const val MAX_SHOW_ILLUST_COUNT = 6

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IllustWidget(
    title: String,
    endText: String,
    navToPictureScreen: (Illust, String) -> Unit,
    illusts: List<Illust>,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = 16.dp
    Column(
        modifier = modifier
    ) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Text(
                    text = endText,
                    fontSize = 12.sp,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .size(12.dp),
                    tint = Color.Blue
                )
            }

        }
        FlowRow(
            Modifier.padding(top = 10.dp),
            maxItemsInEachRow = SPAN_COUNT,
        ) {
            illusts.take(MAX_SHOW_ILLUST_COUNT).forEach {
                val illust = it
                val isBookmarked = requireBookmarkState[illust.id] ?: illust.isBookmarked
                SquareIllustItem(
                    illust = illust,
                    isBookmarked = isBookmarked,
                    onBookmarkClick = { restrict: String, tags: List<String>? ->
                        if (isBookmarked) {
                            BookmarkState.deleteBookmarkIllust(illust.id)
                        } else {
                            BookmarkState.bookmarkIllust(illust.id, restrict, tags)
                        }
                    },
                    spanCount = SPAN_COUNT,
                    horizontalPadding = horizontalPadding,
                    navToPictureScreen = navToPictureScreen,
                )
            }
        }
    }
}
