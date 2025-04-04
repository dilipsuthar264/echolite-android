package com.echolite.app.ui.screens.searchScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.echolite.app.R
import com.echolite.app.navigation.AlbumScreenRoute
import com.echolite.app.navigation.ArtistProfileScreenRoute
import com.echolite.app.ui.components.AppBar
import com.echolite.app.ui.components.VerticalSpace
import com.echolite.app.ui.screens.searchScreen.components.AlbumListView
import com.echolite.app.ui.screens.searchScreen.components.ArtistsListView
import com.echolite.app.ui.screens.searchScreen.components.SearchField
import com.echolite.app.ui.screens.searchScreen.components.SongsListView
import com.echolite.app.ui.screens.searchScreen.data.SearchCategory
import com.echolite.app.ui.screens.searchScreen.viewmodel.SearchViewModel
import com.echolite.app.utils.dynamicPadding

@Composable
fun SearchScreen(
    navController: NavHostController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchState = rememberSaveable { mutableStateOf("") }
    val selectedCategory = rememberSaveable { mutableStateOf(SearchCategory.SONGS) }

    val songs = viewModel.getSongs().collectAsLazyPagingItems()
    val artists = viewModel.getArtists().collectAsLazyPagingItems()
    val albums = viewModel.getAlbum().collectAsLazyPagingItems()
    val currentTrack by viewModel.musicPlayerStateHolder.currentTrack.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }


    // Launch effect to focus and show keyboard when the screen is opened
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }


    Scaffold(topBar = {
        AppBar(
            heading = stringResource(R.string.search),
            isBackNavigation = true,
            navController = navController
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .dynamicPadding(paddingValues)
        ) {
            SearchField(
                focusRequester,
                searchState,
                onSearch = {
                    viewModel.updateQueryModel(
                        viewModel.queryModel.value.copy(
                            query = searchState.value,
                        )
                    )
                }
            )
            VerticalSpace(20.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SearchCategory.entries.forEach { category ->
                    Text(
                        category.displayText,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 5.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .clickable { selectedCategory.value = category }
                            .let {
                                if (category == selectedCategory.value) {
                                    it.border(
                                        1.dp,
                                        MaterialTheme.colorScheme.onBackground,
                                        RoundedCornerShape(20.dp)
                                    )
                                } else {
                                    it
                                }
                            }
                            .padding(vertical = 5.dp)
                    )
                }
            }
            VerticalSpace(20.dp)
            HorizontalDivider()
            when (selectedCategory.value) {
                SearchCategory.SONGS -> {
                    SongsListView(
                        songs,
                        currentTrack = currentTrack,
                        modifier = Modifier.weight(1f),
                        onClick = { song ->
                            song?.let {
                                viewModel.musicPlayerStateHolder.playTrack(
                                    it,
                                    songs.itemSnapshotList.items
                                )
                            }
                        }
                    )
                }

                SearchCategory.ARTIST -> {
                    ArtistsListView(
                        artists,
                        onClick = {
                            navController.navigate(ArtistProfileScreenRoute(it))
                        }
                    )
                }

                SearchCategory.ALBUM -> {
                    AlbumListView(albums, modifier = Modifier.weight(1f), onClick = {
                        navController.navigate(AlbumScreenRoute(it ?: ""))
                    })
                }
            }
        }
    }
}
