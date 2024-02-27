package com.mrl.pixiv.common_ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mrl.pixiv.common.middleware.bookmark.BookmarkAction
import com.mrl.pixiv.common.middleware.bookmark.BookmarkState
import com.mrl.pixiv.data.Illust
import com.mrl.pixiv.util.click

@Composable
fun SquareIllustItem(
    illust: Illust,
    bookmarkState: BookmarkState,
    dispatch: (BookmarkAction) -> Unit,
    spanCount: Int,
    horizontalPadding: Dp = 0.dp,
    paddingValues: PaddingValues = PaddingValues(1.dp),
    elevation: Dp = 5.dp,
    navToPictureScreen: (Illust) -> Unit,
) {
    val isBookmarked = bookmarkState.bookmarkStatus[illust.id] ?: illust.isBookmarked
    val onBookmarkClick: () -> Unit = {
        dispatch(
            if (isBookmarked) {
                BookmarkAction.IllustBookmarkDeleteIntent(illust.id)
            } else {
                BookmarkAction.IllustBookmarkAddIntent(illust.id)
            }
        )
    }
    val onClick = {
        navToPictureScreen(illust.copy(isBookmarked = isBookmarked))
    }
    ConstraintLayout(
        modifier = Modifier
            .padding(paddingValues)
            .graphicsLayer(
                shadowElevation = with(LocalDensity.current) {
                    elevation.toPx()
                },
                shape = MaterialTheme.shapes.medium,
                clip = false
            )
            .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .click { onClick() }
    ) {
        val (image, imageCountText, bookmark) = createRefs()
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val size =
            (screenWidth - horizontalPadding * 2 - 2 * spanCount * paddingValues.calculateLeftPadding(
                LayoutDirection.Ltr
            ) - 1.dp) / spanCount
        AsyncImage(
            modifier = Modifier
                .size(size)
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            model = ImageRequest.Builder(LocalContext.current)
                .data(illust.imageUrls.squareMedium).allowRgb565(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        if (illust.pageCount > 1) {
            Row(
                modifier = Modifier
                    .constrainAs(imageCountText) {
                        top.linkTo(image.top)
                        end.linkTo(image.end)
                    }
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 5.dp),
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
        IconButton(
            modifier = Modifier
                .constrainAs(bookmark) {
                    bottom.linkTo(image.bottom)
                    end.linkTo(image.end)
                },
            onClick = { onBookmarkClick() }
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