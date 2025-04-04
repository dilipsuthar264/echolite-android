package com.echolite.app.ui.screens.dashboard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.echolite.app.R
import com.echolite.app.navigation.AlbumScreenRoute
import com.echolite.app.navigation.ArtistProfileScreenRoute
import com.echolite.app.navigation.FavoriteScreenRoute
import com.echolite.app.navigation.SearchScreenRoute
import com.echolite.app.room.viewmodel.RecentViewModel
import com.echolite.app.ui.components.HorizontalAlbumItem
import com.echolite.app.ui.components.HorizontalArtistItem
import com.echolite.app.ui.components.HorizontalSpace
import com.echolite.app.ui.components.SongListItem
import com.echolite.app.ui.components.VerticalSpace
import com.echolite.app.utils.dynamicImePadding
import com.echolite.app.utils.getViewModelStoreOwner
import com.echolite.app.utils.singleClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    recentViewModel: RecentViewModel = hiltViewModel(navController.getViewModelStoreOwner())
) {
    val recentAlbums by recentViewModel.recentPlayedAlbums.collectAsStateWithLifecycle()
    val recentArtists by recentViewModel.recentPlayedArtists.collectAsStateWithLifecycle()
    val recentSongs by recentViewModel.recentPlayedSongs.collectAsStateWithLifecycle()
    val currentTrack by recentViewModel.musicPlayerStateHolder.currentTrack.collectAsStateWithLifecycle()
    val isLoading by recentViewModel.isLoading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        recentViewModel.fetchData()
    }

    Scaffold(
        topBar = {}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .dynamicImePadding(paddingValues)
                .fillMaxSize()
                .animateContentSize()
        ) {
            VerticalSpace(10.dp)
            DashboardAppBar(navController)
            VerticalSpace(20.dp)
            SearchBar(navController)
            VerticalSpace(10.dp)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                item {
                    if (recentAlbums.isNotEmpty()) {
                        Text(
                            "Recently played albums",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                HorizontalSpace(10.dp)
                            }
                            items(recentAlbums) { album ->
                                HorizontalAlbumItem(
                                    name = album.name ?: "",
                                    image = album.image.getOrNull(1) ?: "",
                                    onClick = singleClick {
                                        album.albumId?.let {
                                            navController.navigate(AlbumScreenRoute(it))
                                        }
                                    }
                                )
                            }
                            item {
                                HorizontalSpace(10.dp)
                            }
                        }
                    }
                }
                item {
                    if (recentArtists.isNotEmpty()) {
                        Text(
                            "Recently played artist",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                        LazyRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            item {
                                HorizontalSpace(10.dp)
                            }
                            items(recentArtists) { artist ->
                                HorizontalArtistItem(
                                    name = artist.name ?: "",
                                    image = artist.image.getOrNull(1) ?: "",
                                    onClick = {
                                        artist.artistId?.let {
                                            navController.navigate(ArtistProfileScreenRoute(it))
                                        }
                                    }
                                )
                            }
                            item {
                                HorizontalSpace(10.dp)
                            }
                        }
                    }
                }
                item {
                    if (recentSongs.isNotEmpty()) {
                        Text(
                            "Recently played songs",
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
                items(recentSongs) { song ->
                    if (recentSongs.isNotEmpty()) {
                        SongListItem(
                            song = song,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            isArtist = true,
                            isPlaying = song.songId == currentTrack?.id,
                            onClick = {
                                recentViewModel.musicPlayerStateHolder.playTrack(
                                    song = song,
                                    songList = recentSongs
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = singleClick { navController.navigate(SearchScreenRoute) })
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(R.drawable.ic_search_outline),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
        )
        HorizontalSpace(10.dp)
        Text(
            text = stringResource(R.string.search_song_artist_album),
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun DashboardAppBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            stringResource(R.string.app_name),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Icon(
            painterResource(R.drawable.ic_heart_filled),
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.onBackground,
                    RoundedCornerShape(10.dp)
                )
                .clickable(
                    onClick = singleClick {
                        navController.navigate(FavoriteScreenRoute)
                    }
                )
                .padding(10.dp)
        )
    }
}