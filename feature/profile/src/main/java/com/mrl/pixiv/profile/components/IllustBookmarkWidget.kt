package com.mrl.pixiv.profile.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common_ui.item.SquareIllustItem
import com.mrl.pixiv.data.Illust

private const val SPAN_COUNT = 3
private const val MAX_SHOW_ILLUST_COUNT = 6

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IllustBookmarkWidget(
    bookmarkState: BookmarkState,
    bookmarkDispatch: (BookmarkAction) -> Unit,
    navToPictureScreen: (Illust) -> Unit,
    illusts: List<Illust>,
) {
    val horizontalPadding = 16.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "插画·漫画收藏",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Text(
                    text = "查看全部",
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
                SquareIllustItem(
                    illust = illust,
                    bookmarkState = bookmarkState,
                    dispatch = bookmarkDispatch,
                    spanCount = SPAN_COUNT,
                    horizontalPadding = horizontalPadding,
                    navToPictureScreen = navToPictureScreen,
                )
            }
        }
    }
}
