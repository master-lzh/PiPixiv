package com.mrl.pixiv.common_ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mrl.pixiv.util.click

@Composable
fun SquareIllustItem(
    isBookmark: Boolean,
    spanCount: Int,
    url: String,
    imageCount: Int,
    horizontalPadding: Dp = 0.dp,
    paddingValues: PaddingValues = PaddingValues(1.dp),
    elevation: Dp = 0.dp,
    onBookmarkClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {

    ConstraintLayout(
        modifier = Modifier
            .padding(paddingValues)
            .clip(MaterialTheme.shapes.medium)
            .shadow(elevation)
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
                .data(url).allowRgb565(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        if (imageCount > 1) {
            Row(
                modifier = Modifier
                    .constrainAs(imageCountText) {
                        top.linkTo(image.top)
                        end.linkTo(image.end)
                    }
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.FileCopy,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(10.dp)
                )
                Text(text = "$imageCount", color = Color.White, fontSize = 10.sp)
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
                imageVector = if (isBookmark) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                contentDescription = "",
                modifier = Modifier.size(24.dp),
                tint = if (isBookmark) Color.Red else Color.Gray
            )
        }
    }
}