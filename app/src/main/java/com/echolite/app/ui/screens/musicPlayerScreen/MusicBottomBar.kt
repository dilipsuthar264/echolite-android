package com.echolite.app.ui.screens.musicPlayerScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.echolite.app.R
import com.echolite.app.ui.components.HorizontalSpace
import com.echolite.app.ui.components.ShowLoader
import com.echolite.app.ui.screens.musicPlayerScreen.viewmodel.MusicPlayerViewModel
import com.echolite.app.utils.middleItem

@Composable
fun MusicBottomBar(viewModel: MusicPlayerViewModel = hiltViewModel(), onClick: () -> Unit) {
    val track by viewModel.musicPlayerStateHolder.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.musicPlayerStateHolder.isPlaying.collectAsStateWithLifecycle()
    val isLoading by viewModel.musicPlayerStateHolder.isLoading.collectAsStateWithLifecycle()
    Row(
        modifier = Modifier
//            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            rememberAsyncImagePainter(track?.image?.middleItem()?.url),
            contentDescription = track?.name,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        HorizontalSpace(10.dp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                track?.name ?: "",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                track?.artists?.primary?.first()?.name ?: "",
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            painterResource(
                R.drawable.ic_previous
            ),
            contentDescription = "Prev",
            modifier = Modifier
                .size(30.dp)
                .clickable { viewModel.musicPlayerStateHolder.prev() }
        )
        HorizontalSpace(15.dp)
        if (isLoading) {
            ShowLoader()
        } else {
            Icon(
                painterResource(
                    if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                ),
                contentDescription = "Play",
                modifier = Modifier
                    .size(30.dp)
                    .clickable { viewModel.musicPlayerStateHolder.playPause() }
            )
        }
        HorizontalSpace(15.dp)
        Icon(
            painterResource(R.drawable.ic_next),
            contentDescription = "Next",
            modifier = Modifier
                .size(30.dp)
                .clickable { viewModel.musicPlayerStateHolder.next() }
        )
    }
}


