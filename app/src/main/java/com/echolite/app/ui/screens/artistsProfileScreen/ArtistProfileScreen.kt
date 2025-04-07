package com.echolite.app.ui.screens.artistsProfileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.echolite.app.R
import com.echolite.app.data.model.QueryModel
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.navigation.AlbumScreenRoute
import com.echolite.app.navigation.ArtistAlbumScreenRoute
import com.echolite.app.navigation.ArtistProfileScreenRoute
import com.echolite.app.navigation.ArtistSongsScreenRoute
import com.echolite.app.ui.components.AppBar
import com.echolite.app.ui.components.CustomListItem
import com.echolite.app.ui.components.HorizontalAlbumItem
import com.echolite.app.ui.components.HorizontalSpace
import com.echolite.app.ui.components.NowPlayingAnimation
import com.echolite.app.ui.components.ShowBottomLoader
import com.echolite.app.ui.components.VerticalSpace
import com.echolite.app.ui.screens.artistsProfileScreen.viewmodel.ArtistViewModel
import com.echolite.app.utils.getViewModelStoreOwner
import com.echolite.app.utils.middleItem
import com.echolite.app.utils.singleClick
import com.echolite.app.utils.updatePlayingSong

@Composable
fun ArtistProfileScreen(
    navController: NavHostController,
    args: ArtistProfileScreenRoute,
    viewModel: ArtistViewModel = hiltViewModel(
        key = args.artistId,
        viewModelStoreOwner = navController.getViewModelStoreOwner()
    )
) {
    val artistDetails by viewModel.artistDetails.collectAsStateWithLifecycle()
    val lazyState = rememberLazyListState()
    val currentSong by viewModel.musicPlayerStateHolder.currentTrack.collectAsStateWithLifecycle()

    var firstTimeFetch by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(firstTimeFetch) {
        if (firstTimeFetch) {
            viewModel.getArtistById(
                artistId = args.artistId,
                queryModel = QueryModel(
                    songCount = 5,
                    albumCount = 5
                )
            )
            viewModel.updateArtistId(args.artistId)
            firstTimeFetch = false
        }
    }

    Scaffold(
        topBar = {
            Column {
                AppBar("", navController)
                artistDetails.data?.let {
                    ArtistImageAndName(
                        it,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    VerticalSpace(20.dp)
                    HorizontalDivider()
                }
            }
        }
    ) { paddingValues ->
        if (artistDetails.isLoading()) {
            Popup(
                alignment = Alignment.Center
            ) {
                ShowBottomLoader()
            }
        }
        artistDetails.data?.let { data ->
            LazyColumn(
                state = lazyState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                artistSongSection(
                    artistDetails = data,
                    currentSong = currentSong,
                    onMoreClick = singleClick {
                        artistDetails.data?.id?.let {
                            navController.navigate(ArtistSongsScreenRoute(it))
                        }
                    },
                    onClick = {
                        viewModel.musicPlayerStateHolder.playTrack(it, data.topSongs)
                    }
                )
                artistAlbumSection(
                    data,
                    onMoreClick = singleClick {
                        artistDetails.data?.id?.let {
                            navController.navigate(
                                ArtistAlbumScreenRoute(it)
                            )
                        }
                    },
                    onClick = { albumId ->
                        albumId?.let {
                            navController.navigate(AlbumScreenRoute(it))
                        }
                    }
                )
                item {
                    VerticalSpace(20.dp)
                }
            }
        }
    }
}

private fun LazyListScope.artistSongSection(
    artistDetails: ArtistResponseModel,
    currentSong: SongResponseModel?,
    onMoreClick: () -> Unit,
    onClick: (SongResponseModel) -> Unit
) {

    item {
        if (artistDetails.topSongs.isNotEmpty()) {
            VerticalSpace(5.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    stringResource(R.string.popular_songs),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    stringResource(R.string.view_all),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onMoreClick)
                )
            }
            VerticalSpace(5.dp)
        }
    }
    items(items = artistDetails.topSongs.updatePlayingSong(currentSong)) { song ->
        ArtistSongListItem(song) {
            onClick(song)
        }
    }
}

@Composable
fun ArtistSongListItem(song: SongResponseModel, onClick: () -> Unit) {
    CustomListItem(
        title = song.name ?: "",
        titleStyle = TextStyle(
            fontSize = 14.sp
        ),
        subtitleStyle = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        subtitle = song.album?.name ?: "",
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp),
        leadingContent = {
            Image(
                rememberAsyncImagePainter(song.image?.middleItem()?.url,
                    placeholder = painterResource(R.drawable.ic_play),
                    error = painterResource(R.drawable.ic_play)),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(40.dp)
            )
        },
        trailingContent = {
            if (song.isPlaying) {
                NowPlayingAnimation()
            }
        },
        onClick = onClick
    )
}

private fun LazyListScope.artistAlbumSection(
    data: ArtistResponseModel,
    onMoreClick: () -> Unit,
    onClick: (String?) -> Unit
) {
    item {
        if (data.topAlbums.isNotEmpty()) {
            VerticalSpace(5.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Popular Albums",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "View all",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onMoreClick)
                )
            }
            VerticalSpace(10.dp)
        }
    }
    item {
        LazyRow(
            Modifier.fillMaxWidth()
        ) {
            item {
                HorizontalSpace(10.dp)
            }
            items(items = data.topAlbums) { album ->
                HorizontalAlbumItem(
                    name = album.name ?: "",
                    image = album.image?.get(1)?.url ?: "",
                    onClick = singleClick {
                        onClick(album.id)
                    }
                )
            }
            item {
                HorizontalSpace(10.dp)
            }
        }
    }
}

@Composable
private fun ArtistImageAndName(data: ArtistResponseModel, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                data.image?.lastOrNull()?.url,
                placeholder = painterResource(R.drawable.ic_user)
            ),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp))
        )
        VerticalSpace(10.dp)
        Text(
            data.name ?: "",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
