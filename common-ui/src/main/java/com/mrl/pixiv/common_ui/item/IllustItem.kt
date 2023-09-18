package com.mrl.pixiv.common_ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FileCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mrl.pixiv.util.DisplayUtil
import com.mrl.pixiv.util.click

@Composable
fun IllustItem(
    spanCount: Int,
    url: String,
    imageCount: Int,
    onClick: () -> Unit = { }
) {
    Box(
        modifier = Modifier
            .padding(1.dp)
            .click { onClick() }
    ) {
        AsyncImage(
            modifier = Modifier.size(DisplayUtil.getScreenWidthDp() / spanCount),
            model = ImageRequest.Builder(LocalContext.current)
                .data(url).allowRgb565(true).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        if (imageCount > 1) {
            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(5.dp)
                    .align(Alignment.TopEnd)
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
    }
}