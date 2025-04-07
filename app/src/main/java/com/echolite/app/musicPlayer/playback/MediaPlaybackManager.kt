package com.echolite.app.musicPlayer.playback

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.musicPlayer.debouncer.Debouncer
import com.echolite.app.musicPlayer.notification.MusicNotificationManager
import com.echolite.app.musicPlayer.playlist.PlaylistManager
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

    private var mediaPlayer = MediaPlayer()

    private lateinit var audioManager: AudioManager
    private var focusRequest: AudioFocusRequest? = null

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

    // Audio focus change listener
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss of focus (e.g., another app starts playing)
                pause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Temporary loss of focus (e.g., notification sound)
                pause()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Temporary loss where ducking (lowering volume) is allowed
                mediaPlayer.setVolume(0.2f, 0.2f) // Lower volume
            }

            AudioManager.AUDIOFOCUS_GAIN -> {
                // Regained focus, resume playback if it was paused
                if (!mediaPlayer.isPlaying && isPlaying.value) {
                    mediaPlayer.start()
                    mediaPlayer.setVolume(1.0f, 1.0f) // Restore volume
                    isPlaying.update { true }
                }
            }
        }
    }

    init {
        // Initialize AudioManager
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private fun requestAudioFocus(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttribute)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build()
            audioManager.requestAudioFocus(focusRequest!!) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                audioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }
    }

    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusChangeListener)
        }
    }

    fun play(track: SongResponseModel) {
        if (Debouncer.shouldIgnoreCall(MusicServiceConst.PLAY)) return
        try {
            if (!requestAudioFocus()) {
                Log.e(TAG, "Audio focus request failed")
                return
            }
            isLoading.update { true }
            mediaPlayer.reset()
            mediaPlayer = MediaPlayer()
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

    private fun pause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying.value = false
            playlistManager.currentTrack.value?.let {
                notificationManager.sendNotification(
                    it,
                    mediaPlayer
                )
            }
        }
    }

    fun playPause() {
        if (Debouncer.shouldIgnoreCall(MusicServiceConst.PLAY_PAUSE)) return
        try {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                isPlaying.value = false
            } else if (mediaPlayer.currentPosition > 0) {
                if (requestAudioFocus()) {
                    mediaPlayer.start()
                    isPlaying.value = true
                    updateDuration()
                }
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
