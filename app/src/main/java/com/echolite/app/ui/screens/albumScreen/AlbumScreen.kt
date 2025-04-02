package com.echolite.app.ui.screens.albumScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.echolite.app.navigation.AlbumScreenRoute
import com.echolite.app.ui.components.AppBar
import com.echolite.app.ui.components.HorizontalSpace
import com.echolite.app.ui.components.ShowBottomLoader
import com.echolite.app.ui.components.SongListItem
import com.echolite.app.ui.components.VerticalSpace
import com.echolite.app.ui.screens.albumScreen.viewmodel.AlbumViewModel
import com.echolite.app.utils.dynamicImePadding
import com.echolite.app.utils.updatePlayingSong

@Composable
fun AlbumScreen(
    navController: NavController,
    args: AlbumScreenRoute,
    viewModel: AlbumViewModel = hiltViewModel(),
) {

    val albumDetails by viewModel.albumDetails.collectAsStateWithLifecycle()
    val currentTrack by viewModel.musicPlayerStateHolder.currentTrack.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (albumDetails.data == null) {
            viewModel.getAlbumById(args.albumId)
        }
    }


    Scaffold(topBar = {
        AppBar("", navController)
    }) { paddingValues ->
        if (albumDetails.isLoading()) {
            Popup(
                alignment = Alignment.Center
            ) {
                ShowBottomLoader()
            }
        }

        albumDetails.data?.let { albumDetails ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .dynamicImePadding(paddingValues)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    AsyncImage(
                        model = albumDetails.image?.get(1)?.url,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp)
                    )
                    HorizontalSpace(20.dp)
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            albumDetails.type?.uppercase() ?: "",
                            fontSize = 10.sp,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(horizontal = 10.dp)
                        )
                        VerticalSpace(10.dp)
                        Text(
                            albumDetails.name ?: "",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        VerticalSpace(5.dp)
                        Text(albumDetails.artists?.all?.mapNotNull { it.name }
                            ?.joinToString(", ") ?: "",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                VerticalSpace(20.dp)
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        VerticalSpace(10.dp)
                    }
                    items(albumDetails.songs.updatePlayingSong(currentTrack)) { song ->
                        SongListItem(
                            song = song,
                            isArtist = true,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                        ) {
                            viewModel.musicPlayerStateHolder.playTrack(
                                song = song,
                                songList = albumDetails.songs
                            )
                        }
                    }
                    item {
                        VerticalSpace(20.dp)
                    }
                }
            }
        }
    }

}