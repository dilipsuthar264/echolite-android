package com.echolite.app.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.echolite.app.R
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.SongArtistsModel
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.room.entities.FavoriteSongEntity
import com.echolite.app.room.entities.SongEntity
import com.echolite.app.utils.fromJson
import com.echolite.app.utils.middleItem


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
            song?.artists?.primary?.joinToString(", ") { it.name ?: "" } ?: ""
        } else {
            song?.album?.name ?: ""
        },
        modifier = modifier,
        leadingContent = {
            Image(
                rememberAsyncImagePainter(
                    song?.image?.middleItem()?.url,
                    placeholder = painterResource(R.drawable.ic_play),
                    error = painterResource(R.drawable.ic_play)
                ),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(45.dp)
            )
        },
        trailingContent = {
            if (song?.isPlaying == true) {
                NowPlayingAnimation(modifier = Modifier.padding(end = 10.dp))
            }
        },
        onClick = onClick
    )
}


@Composable
fun SongListItem(
    song: SongEntity?,
    modifier: Modifier,
    isArtist: Boolean = true,
    isPlaying: Boolean = false,
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
            val songArtist = song?.songArtistsModel.fromJson<SongArtistsModel>()
            songArtist?.primary?.map { it.name }?.joinToString(", ") ?: ""
        } else {
            val songAlbum = song?.albumResponseModel.fromJson<AlbumResponseModel>()
            songAlbum?.name ?: ""
        },
        modifier = modifier,
        leadingContent = {
            Image(
                rememberAsyncImagePainter(
                    song?.image?.middleItem() ?: "",
                    placeholder = painterResource(R.drawable.ic_play),
                    error = painterResource(R.drawable.ic_play)
                ),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(45.dp)
            )
        },
        trailingContent = {
            if (isPlaying) {
                NowPlayingAnimation(modifier = Modifier.padding(end = 10.dp))
            }
        },
        onClick = onClick
    )
}


@Composable
fun SongListItem(
    song: FavoriteSongEntity?,
    modifier: Modifier,
    isArtist: Boolean = true,
    isPlaying: Boolean = false,
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
            val songArtist = song?.songArtistsModel.fromJson<SongArtistsModel>()
            songArtist?.primary?.firstOrNull()?.name ?: ""
        } else {
            val songAlbum = song?.albumResponseModel.fromJson<AlbumResponseModel>()
            songAlbum?.name ?: ""
        },
        modifier = modifier,
        leadingContent = {
            Image(
                rememberAsyncImagePainter(
                    song?.image?.middleItem() ?: "",
                    placeholder = painterResource(R.drawable.ic_play),
                    error = painterResource(R.drawable.ic_play)
                ),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(45.dp)
            )
        },
        trailingContent = {
            if (isPlaying) {
                NowPlayingAnimation(modifier = Modifier.padding(end = 10.dp))
            }
        },
        onClick = onClick
    )
}