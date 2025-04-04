package com.echolite.app.ui.screens.favoriteScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.echolite.app.R
import com.echolite.app.ui.components.AppBar
import com.echolite.app.ui.components.SongListItem
import com.echolite.app.ui.screens.favoriteScreen.viewmodel.FavoriteViewModel
import com.echolite.app.ui.screens.musicPlayerScreen.viewmodel.MusicPlayerViewModel
import com.echolite.app.utils.dynamicPadding
import com.echolite.app.utils.getViewModelStoreOwner

@Composable
fun FavoriteScreen(
    navController: NavHostController,
    viewmodel: FavoriteViewModel = hiltViewModel(navController.getViewModelStoreOwner()),
    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel()
) {
    val favSongs by viewmodel.fetchFavSong.collectAsStateWithLifecycle()

    val currentTrack by musicPlayerViewModel.musicPlayerStateHolder.currentTrack.collectAsStateWithLifecycle()

    Scaffold(topBar = {
        AppBar(
            heading = stringResource(R.string.favorite_songs),
            navController,
            elevation = true
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .dynamicPadding(paddingValues)
        ) {
            items(favSongs) { song ->
                SongListItem(
                    song = song,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    isPlaying = currentTrack?.id == song.songId,
                    isArtist = true,
                    onClick = {
                        musicPlayerViewModel.musicPlayerStateHolder.playTrack(song, favSongs)
                    }
                )
            }
        }
    }
}