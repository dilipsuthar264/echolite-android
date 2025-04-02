package com.echolite.app.ui.screens.musicPlayerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.echolite.app.R
import com.echolite.app.navigation.ArtistProfileScreenRoute
import com.echolite.app.ui.components.HorizontalSpace
import com.echolite.app.ui.components.SongListItem
import com.echolite.app.ui.components.VerticalSpace
import com.echolite.app.ui.screens.musicPlayerScreen.viewmodel.MusicPlayerViewModel
import com.echolite.app.utils.findNextSong
import com.echolite.app.utils.formatTime
import com.echolite.app.utils.updatePlayingSong

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerBottomSheet(
    navController: NavHostController,
    viewmodel: MusicPlayerViewModel = hiltViewModel(), onDismiss: () -> Unit
) {
    val currentTrack by viewmodel.musicPlayerStateHolder.currentTrack.collectAsStateWithLifecycle()
    val currentDuration by viewmodel.musicPlayerStateHolder.currentDuration.collectAsStateWithLifecycle()
    val maxDuration by viewmodel.musicPlayerStateHolder.maxDuration.collectAsStateWithLifecycle()
    val songList by viewmodel.musicPlayerStateHolder.songList.collectAsStateWithLifecycle()
    val nextSong = findNextSong(songList, currentTrack)

    // show show queue
    var showQueue by remember { mutableStateOf(false) }
    if (showQueue) {
        QueueBottomSheet(
            songList = songList.updatePlayingSong(currentTrack),
            onDismiss = { showQueue = false },
            onClick = { song ->
                viewmodel.musicPlayerStateHolder.playTrack(song, songList)
            }
        )
    }

    var showArtistList by remember { mutableStateOf(false) }
    if (showArtistList) {
        currentTrack?.artists?.primary?.let {
            ArtistsBottomSheet(
                artist = it,
                onClick = { artistId ->
                    showArtistList = false
                    if (!artistId.isNullOrEmpty()) {
                        onDismiss()
                        navController.navigate(ArtistProfileScreenRoute(artistId)) {
                            launchSingleTop = false
                        }
                    }
                },
                onDismiss = { showArtistList = false }
            )
        }
    }

    ModalBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        onDismissRequest = onDismiss,
        dragHandle = {},
        shape = RoundedCornerShape(0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalSpace(50.dp)
            AsyncImage(
                model = currentTrack?.image?.last()?.url,
                contentDescription = currentTrack?.name,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .clip(
                        RoundedCornerShape(
                            20.dp
                        )
                    )
            )
            VerticalSpace(20.dp)
            Text(
                currentTrack?.name ?: "",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            VerticalSpace(10.dp)
            Text(
                currentTrack?.artists?.primary?.mapNotNull { it.name }?.joinToString(", ") ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { showArtistList = true }
            )
            VerticalSpace(40.dp)
            DurationSlider(currentDuration, maxDuration) {
                viewmodel.musicPlayerStateHolder.seekTo(it)
            }
            VerticalSpace(20.dp)
            ActionsBtns(viewmodel)
            VerticalSpace(20.dp)
            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.up_next),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    stringResource(R.string.queue),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { showQueue = true }
                        .padding(top = 2.dp, bottom = 4.dp, start = 12.dp, end = 12.dp),
                )
            }
            VerticalSpace(10.dp)
            nextSong?.let {
                SongListItem(
                    nextSong,
                    isArtist = false,
                    modifier = Modifier.padding(vertical = 10.dp),
                    onClick = {
                        viewmodel.musicPlayerStateHolder.playTrack(it, songList)
                    }
                )
            } ?: run {
                Text(
                    "No next song in the queue.",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DurationSlider(
    currentDuration: Float, maxDuration: Float, onSeek: (Float) -> Unit
) {
    var tempCurrentDuration by remember { mutableFloatStateOf(currentDuration) }
    var isUserInteracting by remember { mutableStateOf(false) }

    LaunchedEffect(currentDuration) {
        if (!isUserInteracting) {
            tempCurrentDuration = currentDuration
        }
    }

    Slider(value = tempCurrentDuration, onValueChange = {
        isUserInteracting = true
        tempCurrentDuration = it
    }, onValueChangeFinished = {
        isUserInteracting = false
        onSeek(tempCurrentDuration)
    }, valueRange = 0f..maxDuration, modifier = Modifier
        .fillMaxWidth()
        .height(20.dp), thumb = {
        Box(
            Modifier
                .size(15.dp)
                .background(Color.White, CircleShape)
        )
    }, track = { sliderState ->
        val fraction =
            (sliderState.value - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)

        Box(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .fillMaxWidth(fraction)
                    .align(Alignment.CenterStart)
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
            Box(
                Modifier
                    .fillMaxWidth(1f - fraction)
                    .align(Alignment.CenterEnd)
                    .height(2.dp)
                    .background(Color.White, CircleShape)
            )
        }
    })

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            formatTime(tempCurrentDuration), fontSize = 14.sp
        )
        Text(
            formatTime(maxDuration), fontSize = 14.sp
        )
    }
}

@Composable
private fun ActionsBtns(viewmodel: MusicPlayerViewModel) {
    val isPlaying by viewmodel.musicPlayerStateHolder.isPlaying.collectAsStateWithLifecycle()
    val repeatMode by viewmodel.musicPlayerStateHolder.repeatMode.collectAsStateWithLifecycle()
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(R.drawable.ic_heart),
            contentDescription = "liked",
            modifier = Modifier
                .size(25.dp)
                .clickable { },
            tint = MaterialTheme.colorScheme.onBackground,
        )
        HorizontalSpace()
        Icon(
            painterResource(R.drawable.ic_previous),
            contentDescription = "prev",
            modifier = Modifier
                .size(30.dp)
                .clickable { viewmodel.musicPlayerStateHolder.prev() },
            tint = MaterialTheme.colorScheme.onBackground
        )
        Icon(
            painterResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
            contentDescription = "play pause",
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(15.dp)
                .size(30.dp)
                .clickable {
                    viewmodel.musicPlayerStateHolder.playPause()
                },
            tint = MaterialTheme.colorScheme.onBackground
        )
        Icon(
            painterResource(R.drawable.ic_next),
            contentDescription = "next",
            modifier = Modifier
                .size(30.dp)
                .clickable {
                    viewmodel.musicPlayerStateHolder.next()
                },
            tint = MaterialTheme.colorScheme.onBackground
        )
        HorizontalSpace()
        Icon(
            painterResource(repeatMode.icon),
            contentDescription = "repeat and shuffle",
            modifier = Modifier
                .size(25.dp)
                .clickable {
                    viewmodel.musicPlayerStateHolder.toggleRepeat()
                },
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

