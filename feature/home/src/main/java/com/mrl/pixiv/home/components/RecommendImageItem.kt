package com.mrl.pixiv.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.home.state.RecommendImageItemState
import com.mrl.pixiv.util.DisplayUtil
import com.mrl.pixiv.util.click
import com.mrl.pixiv.util.second
import kotlin.io.encoding.ExperimentalEncodingApi

val SPACING_HORIZONTAL_DP = 5.dp
val SPACING_VERTICAL_DP = 5.dp
const val SPAN_COUNT = 2
const val INCLUDE_EDGE = true
val recommendItemWidth =
    (DisplayUtil.getScreenWidthDp() - SPACING_HORIZONTAL_DP * (SPAN_COUNT + if (INCLUDE_EDGE) 1 else -1))/ SPAN_COUNT

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun RecommendImageItem(
    navToPictureScreen: (Illust) -> Unit,
    scaffoldState: ScaffoldState,
    item: RecommendImageItemState,
    onBookmarkClick: (id: Long, bookmark: Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 5.dp)
            .padding(bottom = 5.dp)
            .click {
                navToPictureScreen(item.illust)
            },
        shape = RoundedCornerShape(10.dp),
        elevation = 4.dp
    ) {
        Column {
            val radius = DisplayUtil.dp2px(10f).toFloat()
            Box(
                modifier = Modifier
                    .width(item.width)
                    .height(item.height)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.thumbnail).allowRgb565(true)
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
                        .width(item.width)
                        .height(item.height),
                    filterQuality = FilterQuality.None
                )
            }

            ConstraintLayout(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .fillMaxWidth()
            ) {
                val (titleAuthor, bookmark) = createRefs()
                val barrier = createStartBarrier(bookmark)
                createHorizontalChain(titleAuthor, bookmark, chainStyle = ChainStyle.SpreadInside)
                Column(
                    modifier = Modifier
                        .constrainAs(titleAuthor) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(barrier)
                            this.width = Dimension.preferredWrapContent
                        }
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )

                    Text(
                        text = item.author,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Clip,
                    )
                }

                IconButton(
                    modifier = Modifier
                        .constrainAs(bookmark) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                        },
                    onClick = {
                        onBookmarkClick(item.id, !item.isBookmarked)
                    },
                ) {
                    Icon(
                        imageVector = if (item.isBookmarked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp),
                        tint = if (item.isBookmarked) Color.Red else Color.Gray
                    )
                }
            }
        }
    }
}