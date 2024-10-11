package com.mrl.pixiv.profile.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.mrl.pixiv.data.Novel
import com.mrl.pixiv.profile.R

private const val MAX_SHOW_NOVEL_COUNT = 3

@Composable
fun NovelBookmarkWidget(
    novels: List<Novel>,
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.novel_collection),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                Text(
                    text = stringResource(R.string.view_all),
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
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .padding(top = 5.dp)
        )
        novels.take(MAX_SHOW_NOVEL_COUNT).forEach {
            NovelItem(it)
        }
    }
}

@Composable
private fun NovelItem(
    novel: Novel
) {
    Column {
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp)
        ) {
            Column(
                modifier = Modifier.padding(start = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    modifier = Modifier.height(90.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(novel.imageUrls.medium)
                        .build(),
                    contentDescription = null
                )
                Row(
                    modifier = Modifier.padding(top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Favorite,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp),
                        tint = Color.LightGray
                    )
                    Text(
                        text = novel.totalBookmarks.toString(),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }

            }
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            ) {
                novel.series.title?.let {
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF858585),
                    )
                }
                Text(
                    text = novel.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = if (novel.series.title == null) 0.dp else 5.dp)
                )
                //author
                Text(
                    text = "by ${novel.user.name}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 5.dp)
                )
                //tag
                Text(
                    text = stringResource(
                        R.string.novel_description,
                        novel.textLength,
                        novel.tags.joinToString(" ") { "#${it.name}" }),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 5.dp)
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 15.dp, vertical = 15.dp)
        )
    }

}