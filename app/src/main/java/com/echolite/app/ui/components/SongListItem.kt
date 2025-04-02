package com.echolite.app.ui.components


import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.echolite.app.data.model.response.SongResponseModel


@Composable
fun SongListItem(
    song: SongResponseModel?,
    modifier: Modifier,
    isArtist: Boolean = true,
    onClick: () -> Unit
) {
    CustomListItem(
        title = song?.name ?: "",
        titleStyle = TextStyle(
            fontSize = 16.sp
        ),
        subtitleStyle = TextStyle(
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        subtitle = if (isArtist) {
            song?.artists?.primary?.firstOrNull()?.name ?: ""
        } else {
            song?.album?.name ?: ""
        },
        modifier = modifier,
        leadingContent = {
            AsyncImage(
                model = song?.image?.get(1)?.url,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(45.dp)
            )
        },
        trailingContent = {
            if (song?.isPlaying == true) {
                NowPlayingAnimation( modifier = Modifier.padding(end = 10.dp))
            }
        },
        onClick = onClick
    )
}