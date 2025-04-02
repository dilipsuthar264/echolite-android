package com.echolite.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.musicPlayer.MusicCommand
import com.echolite.app.musicPlayer.MusicPlayerService
import com.echolite.app.musicPlayer.MusicPlayerStateHolder
import com.echolite.app.navigation.NavGraph
import com.echolite.app.ui.screens.musicPlayerScreen.MusicBottomBar
import com.echolite.app.ui.screens.musicPlayerScreen.MusicPlayerBottomSheet
import com.echolite.app.ui.theme.EchoLiteTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var musicStateHolder: MusicPlayerStateHolder

    private var service: MusicPlayerService? = null
    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            service = (binder as MusicPlayerService.MusicBinder).getService()
            lifecycleScope.launch {
                binder.isPlaying().collectLatest {
                    musicStateHolder.updateIsPlaying(it)
                }
            }
            lifecycleScope.launch {
                binder.getRepeatMode().collectLatest {
                    musicStateHolder.updateRepeatMode(it)
                }
            }

            lifecycleScope.launch {
                binder.getSongList().collectLatest {
                    musicStateHolder.updateSongList(it)
                }
            }

            lifecycleScope.launch {
                binder.maxDuration().collectLatest {
                    musicStateHolder.updateMaxDuration(it)
                }
            }
            lifecycleScope.launch {
                binder.currentDuration().collectLatest {
                    musicStateHolder.updateCurrentDuration(it)
                }
            }

            lifecycleScope.launch {
                binder.getCurrentTrack().collectLatest {
                    musicStateHolder.updateCurrentTrack(it)
                }
            }
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        println("onStart")
        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, connection, Context.BIND_AUTO_CREATE or Context.BIND_IMPORTANT)
    }

    override fun onResume() {
        super.onResume()
        print("ON Resume ")
        lifecycleScope.launch {
            musicStateHolder.commands.collect { command ->
                handleMusicCommand(command)
            }
        }
    }

    private fun handleMusicCommand(command: MusicCommand) {
        Log.d("MusicActivity", "Received command: $command")

        if (service == null) {
            Log.e("MusicActivity", "Service is null! Cannot send command")
            return
        }

        when (command) {
            is MusicCommand.PlayTrack -> playSong(command.song, command.songList)
            is MusicCommand.PlayPause -> service?.playPause()
            is MusicCommand.SeekTo -> service?.seekTo(command.position.toInt())
            is MusicCommand.Next -> service?.next()
            is MusicCommand.Previous -> service?.prev()
            is MusicCommand.ToggleRepeat -> service?.toggleRepeatMode()
        }
    }


    private fun playSong(song: SongResponseModel?, songList: List<SongResponseModel>) {
        song?.let { service?.play(it) }
        service?.setList(songList)
    }

    //    override fun onStop() {
//        super.onStop()
//        if (isBound) {
//            unbindService(connection)
//            isBound = false
//        }
//    }
    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            ),
        )
        setContent {
            val currentTrack by musicStateHolder.currentTrack.collectAsState()
            val navController = rememberNavController()
            var isMusicPlayerExtended by remember { mutableStateOf(false) }
            EchoLiteTheme(
                darkTheme = true,
            ) {
                Surface() {
                    if (isMusicPlayerExtended) {
                        MusicPlayerBottomSheet(
                            navController = navController,
                            onDismiss = {
                                isMusicPlayerExtended = false
                            }
                        )
                    }
                    Column(Modifier.fillMaxSize()) {
                        NavGraph(navController, Modifier.weight(1f))
                        AnimatedVisibility(
                            visible = !currentTrack?.name.isNullOrEmpty()
                        ) {
                            HorizontalDivider()
                            MusicBottomBar(
                                onClick = {
                                    isMusicPlayerExtended = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

