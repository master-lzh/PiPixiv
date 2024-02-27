package com.mrl.pixiv.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.common.ui.components.Surface
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.util.DisplayUtil
import com.mrl.pixiv.util.click
import com.mrl.pixiv.util.second

val SPACING_HORIZONTAL_DP = 5.dp
val SPACING_VERTICAL_DP = 5.dp
const val INCLUDE_EDGE = true


@Composable
fun RecommendImageItem(
    navToPictureScreen: (Illust) -> Unit,
    illust: Illust,
    bookmarkState: BookmarkState,
    onBookmarkClick: (id: Long, bookmark: Boolean) -> Unit,
    spanCount: Int,
) {
    val width =
        (DisplayUtil.getScreenWidthDp() - SPACING_HORIZONTAL_DP * (spanCount + if (INCLUDE_EDGE) 1 else -1)) / spanCount
    val scale = illust.height * 1.0f / illust.width
    val height = width * scale
    val isBookmarked = bookmarkState.bookmarkStatus[illust.id] ?: illust.isBookmarked
    Surface(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .padding(bottom = 5.dp)
            .click {
                navToPictureScreen(illust)
            },
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 4.dp,
        propagateMinConstraints = false,
    ) {
        Column {
            val radius = DisplayUtil.dp2px(10f).toFloat()
            Box(
                modifier = Modifier
                    .width(width)
                    .height(height)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(illust.imageUrls.medium).allowRgb565(true)
                        .transformations(
                            RoundedCornersTransformation(
                                topLeft = radius,
                                topRight = radius
                            )
                        )
//                        .scale(Scale.FIT)
                        .crossfade(1.second.toInt())
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .width(width)
                        .height(height),
                    filterQuality = FilterQuality.None
                )
            }
            Row(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .widthIn(max = width)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = width - 40.dp)
                ) {
                    Text(
                        text = illust.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )

                    Text(
                        text = illust.user.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )
                }

                IconButton(
                    onClick = {
                        onBookmarkClick(illust.id, isBookmarked)
                    },
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp),
                        tint = if (isBookmarked) Color.Red else Color.Gray
                    )
                }
            }
        }
        if (illust.pageCount > 1) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 5.dp)
                    .align(Alignment.TopEnd),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.FileCopy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
                Text(text = "${illust.pageCount}", color = Color.White, fontSize = 10.sp)
            }
        }
    }
}