package com.echolite.app.musicPlayer.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.musicPlayer.debouncer.Debouncer
import com.echolite.app.musicPlayer.notification.MusicNotificationManager
import com.echolite.app.musicPlayer.playlist.PlaylistManager
import com.echolite.app.room.repo.RecentSongRepo
import com.echolite.app.room.viewmodel.RecentViewModel
import com.echolite.app.utils.MusicRepeatMode
import com.echolite.app.utils.MusicServiceConst
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaPlaybackManager @Inject constructor(
    private val playlistManager: PlaylistManager,
    private val notificationManager: MusicNotificationManager,
    @ApplicationContext private val context: Context
) {


    val mediaPlayer = MediaPlayer()

    val isPlaying = MutableStateFlow(false)
    val currentDuration = MutableStateFlow(0f)
    val maxDuration = MutableStateFlow(0f)
    val isLoading = MutableStateFlow(false)

    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    private val audioAttribute = AudioAttributes.Builder()
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .build()


    fun play(track: SongResponseModel) {
        if (Debouncer.shouldIgnoreCall(MusicServiceConst.PLAY)) return
        try {
            isLoading.update { true }
            mediaPlayer.reset()
            mediaPlayer.setAudioAttributes(audioAttribute)
            mediaPlayer.setDataSource(
                context, Uri.parse(track.downloadUrl?.lastOrNull()?.url)
            )
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isLoading.update { false }
                mediaPlayer.start()
                isPlaying.update { true }
                updateDuration()
                playlistManager.currentTrack.update { track }
                notificationManager.sendNotification(
                    track,
                    mediaPlayer
                )
            }
            mediaPlayer.setOnCompletionListener {
                when (playlistManager.repeatMode.value) {
                    MusicRepeatMode.REPEAT_ONE -> play(track)
                    else -> next()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "play: $e")
        }
    }


    fun seekTo(position: Int) {
        if (Debouncer.shouldIgnoreCall(MusicServiceConst.SEEK_TO)) return
        if (position in 0..mediaPlayer.duration) {
            mediaPlayer.seekTo(position)
            updateDuration()
            playlistManager.currentTrack.value?.let {
                notificationManager.sendNotification(
                    it,
                    mediaPlayer
                )
            }
        }
    }

    fun prev() {
        if (Debouncer.shouldIgnoreCall(MusicServiceConst.PREV)) return
        job?.cancel()
        val currentTrack = playlistManager.currentTrack.value ?: return
        val prevTrack = playlistManager.getPreviousTrack() ?: return
        play(prevTrack)
    }

    fun next() {
        if (Debouncer.shouldIgnoreCall(MusicServiceConst.NEXT)) return
        job?.cancel()
        val currentTrack = playlistManager.currentTrack.value ?: return
        val nextTrack = playlistManager.getNextTrack() ?: return
        play(nextTrack)
    }

    fun playPause() {
        if (Debouncer.shouldIgnoreCall(MusicServiceConst.PLAY_PAUSE)) return
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                isPlaying.value = false
            } else if (mediaPlayer.currentPosition > 0) {
                mediaPlayer.start()
                isPlaying.value = true
                updateDuration()
            } else {
                playlistManager.currentTrack.value?.let { play(it) }
            }
            playlistManager.currentTrack.value?.let {
                notificationManager.sendNotification(
                    it,
                    mediaPlayer
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "playPause: $e")
        }
    }

    private fun updateDuration() {
        job?.cancel()
        job = scope.launch {
            if (!mediaPlayer.isPlaying) return@launch
            maxDuration.update { mediaPlayer.duration.toFloat() }
            while (true) {
                currentDuration.update { mediaPlayer.currentPosition.toFloat() }
                delay(1000)
            }
        }
    }

    fun release() {
        scope.cancel()
        job?.cancel()
        mediaPlayer.release()
    }


    companion object {
        val TAG: String = MediaPlaybackManager::class.java.name
    }
}
