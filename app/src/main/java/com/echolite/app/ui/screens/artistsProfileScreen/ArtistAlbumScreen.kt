package com.echolite.app.ui.screens.artistsProfileScreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.echolite.app.R
import com.echolite.app.navigation.AlbumScreenRoute
import com.echolite.app.navigation.ArtistAlbumScreenRoute
import com.echolite.app.ui.components.AppBar
import com.echolite.app.ui.screens.artistsProfileScreen.viewmodel.ArtistViewModel
import com.echolite.app.ui.screens.searchScreen.components.AlbumListView
import com.echolite.app.utils.dynamicImePadding
import com.echolite.app.utils.getViewModelStoreOwner

@Composable
fun ArtistAlbumScreen(
    navController: NavHostController,
    args: ArtistAlbumScreenRoute,
    viewmodel: ArtistViewModel = hiltViewModel(viewModelStoreOwner = navController.getViewModelStoreOwner(), key = args.artistId)
) {
    val albums = viewmodel.getArtistAlbums().collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            AppBar(
                heading = stringResource(R.string.albums),
                navController,
                elevation = true
            )
        }
    ) { paddingValues ->
        AlbumListView(
            albums, modifier = Modifier
                .fillMaxWidth()
                .dynamicImePadding(paddingValues),
            onClick = {
                navController.navigate(AlbumScreenRoute(it ?: ""))
            }
        )
    }
}