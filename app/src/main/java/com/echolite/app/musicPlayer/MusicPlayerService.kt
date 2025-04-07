package com.echolite.app.musicPlayer

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.musicPlayer.notification.MusicNotificationManager
import com.echolite.app.musicPlayer.playback.MediaPlaybackManager
import com.echolite.app.musicPlayer.playlist.PlaylistManager
import com.echolite.app.utils.MusicServiceConst
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MusicPlayerService : Service() {

    @Inject
    lateinit var playbackManager: MediaPlaybackManager

    @Inject
    lateinit var playlistManager: PlaylistManager

    @Inject
    lateinit var notificationManager: MusicNotificationManager
    private val binder = MusicBinder()


    inner class MusicBinder : Binder() {
        fun getService() = this@MusicPlayerService
        fun getRepeatMode() = playlistManager.repeatMode
        fun getSongList() = playlistManager.musicList
        fun currentDuration() = playbackManager.currentDuration
        fun maxDuration() = playbackManager.maxDuration
        fun isPlaying() = playbackManager.isPlaying
        fun getCurrentTrack() = playlistManager.currentTrack
        fun isLoading() = playbackManager.isLoading
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                MusicServiceConst.PREV -> playbackManager.prev()
                MusicServiceConst.PLAY_PAUSE -> playbackManager.playPause()
                MusicServiceConst.NEXT -> playbackManager.next()
                MusicServiceConst.CANCEL -> stopService()
            }
        }
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager.initialize(this)
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopService()
    }

    fun play(track: SongResponseModel) = playbackManager.play(track)
    fun seekTo(position: Int) = playbackManager.seekTo(position)
    fun setList(songList: List<SongResponseModel>) = playlistManager.setList(songList)
    fun toggleRepeatMode() = playlistManager.toggleRepeatMode()
    fun next() = playbackManager.next()
    fun prev() = playbackManager.prev()
    fun playPause() = playbackManager.playPause()


    fun stopService() {
        playbackManager.release()
        notificationManager.stop()
        stopForeground(true)
        stopSelf()
    }


    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }
}