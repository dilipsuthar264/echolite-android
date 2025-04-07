package com.echolite.app.ui.screens.favoriteScreen

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.echolite.app.R
import com.echolite.app.ui.components.AppBar
import com.echolite.app.ui.components.EmptyScreen
import com.echolite.app.ui.components.ShowBottomLoader
import com.echolite.app.ui.components.SongListItem
import com.echolite.app.ui.screens.favoriteScreen.viewmodel.FavoriteViewModel
import com.echolite.app.ui.screens.musicPlayerScreen.viewmodel.MusicPlayerViewModel
import com.echolite.app.utils.dynamicPadding
import com.echolite.app.utils.getViewModelStoreOwner
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@Composable
fun FavoriteScreen(
    navController: NavHostController,
    viewmodel: FavoriteViewModel = hiltViewModel(navController.getViewModelStoreOwner()),
    musicPlayerViewModel: MusicPlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val favSongs by viewmodel.fetchFavSong.collectAsStateWithLifecycle()
    val currentTrack by musicPlayerViewModel.musicPlayerStateHolder.currentTrack.collectAsStateWithLifecycle()
    val isLoading by viewmodel.isLoading.collectAsStateWithLifecycle()

    // file import
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            if (uri.toString().endsWith(".echolite")) {
                scope.launch {
                    viewmodel.importSongs(context, uri)
                }
            } else {
                Toast.makeText(context,
                    "Only .echolite backup files are supported (e.g., backup_153627.echolite", Toast.LENGTH_LONG).show()
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            scope.cancel()
        }
    }

    Scaffold(topBar = {
        AppBar(
            heading = stringResource(R.string.liked_songs),
            navController,
            elevation = true,
            isHomeBtn = false,
            actions = {
                ImportExportBtn(
                    onExportClick = {
                        val message = viewmodel.exportSongs(context)
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    },
                    onImportClick = {
                        filePickerLauncher.launch("*/*")
                    }
                )
            }
        )
    }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .dynamicPadding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (isLoading) {
                    ShowBottomLoader()
                }
            }
            item {
                if (favSongs.isEmpty()) {
                    EmptyScreen(
                        text = stringResource(R.string.you_haven_t_liked_any_songs_yet),
                        image = R.drawable.ic_music,
                        iconSize = 100.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(top = 30.dp)
                    )
                }
            }
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

@Composable
private fun RowScope.ImportExportBtn(
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(
            onClick = onExportClick
        )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_export),
            contentDescription = "Export",
            tint = MaterialTheme.colorScheme.onBackground
        )
        Text(
            stringResource(R.string.export),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .clickable(
                onClick = onImportClick
            )
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_import),
            contentDescription = "Import",
            tint = MaterialTheme.colorScheme.onBackground
        )
        Text(
            stringResource(R.string.import_),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}