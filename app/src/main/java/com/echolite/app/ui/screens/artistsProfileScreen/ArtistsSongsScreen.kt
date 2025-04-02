package com.echolite.app.ui.screens.artistsProfileScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.echolite.app.R
import com.echolite.app.navigation.ArtistProfileScreenRoute
import com.echolite.app.navigation.ArtistSongsScreenRoute
import com.echolite.app.ui.components.AppBar
import com.echolite.app.ui.screens.artistsProfileScreen.viewmodel.ArtistViewModel
import com.echolite.app.ui.screens.searchScreen.components.SongsListView
import com.echolite.app.utils.dynamicImePadding
import com.echolite.app.utils.getViewModelStoreOwner

@Composable
fun ArtistsSongsScreen(
    navController: NavController,
    args: ArtistSongsScreenRoute,
    viewModel: ArtistViewModel = hiltViewModel(viewModelStoreOwner = navController.getViewModelStoreOwner(), key = args.artistId)
) {
    val songs = viewModel.getArtistSongs().collectAsLazyPagingItems()
    val currentSong by viewModel.musicPlayerStateHolder.currentTrack.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppBar(
                heading = stringResource(R.string.songs),
                navController,
                elevation = true
            )
        }
    ) { paddingValues ->
        SongsListView(
            songs,
            modifier = Modifier
                .fillMaxSize()
                .dynamicImePadding(paddingValues),
            currentTrack = currentSong,
            onClick = { song ->
                song?.let {
                    viewModel.musicPlayerStateHolder.playTrack(
                        song = it,
                        songList = songs.itemSnapshotList.items
                    )
                }
            }
        )
    }
}