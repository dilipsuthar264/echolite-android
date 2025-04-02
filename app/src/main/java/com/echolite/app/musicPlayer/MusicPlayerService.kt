    package com.echolite.app.musicPlayer

    import android.Manifest
    import android.app.PendingIntent
    import android.app.Service
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
    import android.graphics.drawable.Drawable
    import android.media.AudioAttributes
    import android.media.MediaPlayer
    import android.net.Uri
    import android.os.Binder
    import android.os.Build
    import android.os.IBinder
    import android.support.v4.media.MediaMetadataCompat
    import android.support.v4.media.session.MediaSessionCompat
    import android.support.v4.media.session.PlaybackStateCompat
    import android.util.Log
    import androidx.core.app.NotificationCompat
    import androidx.core.content.ContextCompat
    import com.bumptech.glide.Glide
    import com.bumptech.glide.request.target.CustomTarget
    import com.echolite.app.R
    import com.echolite.app.data.model.response.SongResponseModel
    import com.echolite.app.utils.MusicRepeatMode
    import com.echolite.app.utils.MusicServiceConst
    import com.echolite.app.utils.NotificationChannelConst
    import dagger.hilt.android.AndroidEntryPoint
    import kotlinx.coroutines.CoroutineScope
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.Job
    import kotlinx.coroutines.cancel
    import kotlinx.coroutines.delay
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.update
    import kotlinx.coroutines.launch


    @AndroidEntryPoint
    class MusicPlayerService : Service() {

        var mediaPlayer = MediaPlayer()

        private val currentTrack = MutableStateFlow<SongResponseModel?>(null)
        private var musicList = MutableStateFlow<List<SongResponseModel>>(emptyList())
        private val maxDuration = MutableStateFlow(0f)
        private val currentDuration = MutableStateFlow<Float>(0f)
        private val scope = CoroutineScope(Dispatchers.Main)
        private var isPlaying = MutableStateFlow(false)
        private var repeatMode = MutableStateFlow(MusicRepeatMode.REPEAT)
        private var originalMusicList: MutableList<SongResponseModel> = mutableListOf()

        private var job: Job? = null

        private val binder = MusicBinder()

        inner class MusicBinder : Binder() {
            fun getRepeatMode() = this@MusicPlayerService.repeatMode
            fun getService() = this@MusicPlayerService
            fun getSongList() = this@MusicPlayerService.musicList
            fun currentDuration() = this@MusicPlayerService.currentDuration
            fun maxDuration() = this@MusicPlayerService.maxDuration
            fun isPlaying() = this@MusicPlayerService.isPlaying
            fun getCurrentTrack() = this@MusicPlayerService.currentTrack
        }

        fun setList(songList: List<SongResponseModel>) {
            originalMusicList.clear()
            musicList.update { songList }
            updatePlayListForRepeatMode()
        }

        fun toggleRepeatMode() {
            if (shouldIgnoreCall(MusicServiceConst.REPEAT_MODE)) return
            repeatMode.value = when (repeatMode.value) {
                MusicRepeatMode.REPEAT -> MusicRepeatMode.REPEAT_ONE
                MusicRepeatMode.REPEAT_ONE -> MusicRepeatMode.SHUFFLE
                MusicRepeatMode.SHUFFLE -> MusicRepeatMode.REPEAT
            }
            updatePlayListForRepeatMode()
        }

        private fun updatePlayListForRepeatMode() {
            when (repeatMode.value) {
                MusicRepeatMode.REPEAT -> {
                    if (originalMusicList.isNotEmpty()) {
                        musicList.update { originalMusicList }
                    }
                }

                MusicRepeatMode.SHUFFLE -> shuffleSongList()
                MusicRepeatMode.REPEAT_ONE -> Unit
            }
        }

        private fun shuffleSongList() {
            if (originalMusicList.isEmpty()) {
                originalMusicList = musicList.value.toMutableList()
            }
            val shuffledList = musicList.value.shuffled().toMutableList()
            currentTrack.value?.let {
                shuffledList.remove(it)
                shuffledList.add(0, it)
            }
            musicList.update { shuffledList }
        }

        override fun onBind(intent: Intent?): IBinder = binder

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            intent?.let {
                when (it.action) {
                    MusicServiceConst.PREV -> prev()
                    MusicServiceConst.PLAY_PAUSE -> playPause()
                    MusicServiceConst.NEXT -> next()
                    MusicServiceConst.CANCEL -> stopService()
                }
            }
            return START_NOT_STICKY
        }

        override fun onCreate() {
            super.onCreate()
    //        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
    //        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MusicLock")
    //        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
        }

        override fun onTaskRemoved(rootIntent: Intent?) {
            super.onTaskRemoved(rootIntent)
            stopService()
        }

        private fun stopService() {
            scope.cancel()
            job?.cancel()
            mediaPlayer.release()
            stopForeground(true)
            stopSelf()
        }

        private fun updateDuration() {
            job?.cancel()
            job = scope.launch {
                if (mediaPlayer.isPlaying.not()) return@launch
                maxDuration.update { mediaPlayer.duration.toFloat() }
                while (true) {
                    currentDuration.update { mediaPlayer.currentPosition.toFloat() }
                    delay(1000)
                }
            }
        }

        // Define `audioAttributes` once to avoid redundant creation
        private val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        private val lastFunctionCallTime = mutableMapOf<String, Long>()
        private val debounceTime = 200L

        private fun shouldIgnoreCall(functionName: String): Boolean {
            val currentTime = System.currentTimeMillis()
            if (currentTime - (lastFunctionCallTime[functionName] ?: 0) < debounceTime) {
                Log.d("MusicService", "Ignoring rapid $functionName() calls")
                return true
            }
            lastFunctionCallTime[functionName] = currentTime
            return false
        }

        fun play(track: SongResponseModel) {
            if (shouldIgnoreCall(MusicServiceConst.PLAY)) return
            try {
                mediaPlayer.reset()
                mediaPlayer.setAudioAttributes(audioAttributes)
                mediaPlayer.setDataSource(
                    this@MusicPlayerService,
                    Uri.parse(track.downloadUrl?.get(2)?.url)
                )
                mediaPlayer.prepareAsync()
                mediaPlayer.setOnPreparedListener {
                    mediaPlayer.start()
                    isPlaying.update { true }
                    updateDuration()
                    currentTrack.update { track }
                    sendNotification(track)
                }
                mediaPlayer.setOnCompletionListener {
                    if (repeatMode.value == MusicRepeatMode.REPEAT_ONE) {
                        play(track)
                    } else {
                        next()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MusicService", "Failed to play track: ${e.message}")
            }
        }


        fun seekTo(position: Int) {
            if (shouldIgnoreCall("seekTo")) return
            try {
                if (position in 0..mediaPlayer.duration) {
                    mediaPlayer.seekTo(position)
                    updateDuration()
                    currentTrack.value?.let { sendNotification(it) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MusicService", "Failed to seek track: ${e.message}")
            }
        }

        fun prev() {
            if (shouldIgnoreCall(MusicServiceConst.PREV)) return
            job?.cancel()
            if (musicList.value.isEmpty()) return

            val index = musicList.value.indexOf(currentTrack.value).takeIf { it >= 0 } ?: return
            val prevIndex = if (index == 0) musicList.value.lastIndex else index - 1
            val prevItem = musicList.value.getOrNull(prevIndex) ?: return

            currentTrack.update { prevItem }
            play(prevItem)
        }

        fun next() {
            if (shouldIgnoreCall(MusicServiceConst.NEXT)) return
            Log.e("Music Service", "next: ${musicList.value}")
            job?.cancel()
            if (musicList.value.isEmpty()) return
            val index = musicList.value.indexOf(currentTrack.value).takeIf { it >= 0 } ?: return
            val nextIndex = (index + 1) % musicList.value.size
            val nextItem = musicList.value.getOrNull(nextIndex) ?: return
            currentTrack.update { nextItem }
            play(nextItem)
        }


        fun playPause() {
            if (shouldIgnoreCall(MusicServiceConst.PLAY_PAUSE)) return
            try {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    isPlaying.value = false
                } else {
                    if (mediaPlayer.currentPosition > 0) {
                        mediaPlayer.start()
                        isPlaying.value = true
                        updateDuration()
                    } else {
                        currentTrack.value?.let { play(it) }
                    }
                }
                currentTrack.value?.let { sendNotification(it) }
            } catch (e: Exception) {
                Log.e("MusicService", "Play/Pause error: ${e.message}", e)
            }
        }


        private lateinit var mediaSession: MediaSessionCompat

        private fun initMediaSession() {
            mediaSession = MediaSessionCompat(this, "music").apply {
                setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                )
                isActive = true
            }
        }


        private fun sendNotification(track: SongResponseModel) {
            isPlaying.update { mediaPlayer.isPlaying }

            // Ensure MediaSession is initialized once
            if (!::mediaSession.isInitialized) {
                initMediaSession()
            }
            // Set metadata
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.name)
                    .putString(
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        track.artists?.primary?.first()?.name
                    )
                    .putLong(
                        MediaMetadataCompat.METADATA_KEY_DURATION,
                        mediaPlayer.duration.toLong()
                    ) // Duration for seekbar
                    .build()
            )
            // Set playback state with all actions
            val playbackState = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or
                            PlaybackStateCompat.ACTION_PAUSE or
                            PlaybackStateCompat.ACTION_SEEK_TO or
                            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
                .setState(
                    if (mediaPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                    mediaPlayer.currentPosition.toLong(),
                    1.0f // Playback speed
                )
                .build()
            mediaSession.setPlaybackState(playbackState)


            // Handle seekbar & playback controls
            mediaSession.setCallback(object : MediaSessionCompat.Callback() {
                override fun onSeekTo(pos: Long) {
                    mediaPlayer.seekTo(pos.toInt())
                    currentTrack.value?.let { sendNotification(it) }
                    updateDuration()
                }

                override fun onPause() {
                    playPause()
                }

                override fun onPlay() {
                    playPause()
                }

                override fun onSkipToNext() {
                    next()
                }

                override fun onSkipToPrevious() {
                    prev()
                }

                override fun onStop() {
                    stopService()
                }
            })


            val style = androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSession.sessionToken)
                .setShowCancelButton(true)
                .setCancelButtonIntent(createCancelPendingIntent())

            // Assuming imageUrl is your image string
            val imageUrl = track.image?.last()?.url

            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        val notification =
                            NotificationCompat.Builder(
                                this@MusicPlayerService,
                                NotificationChannelConst.CHANNEL_ID
                            )
                                .setStyle(style)
                                .setContentTitle(track.name)
                                .setContentText(track.artists?.primary?.first()?.name)
                                .addAction(R.drawable.ic_previous, "prev", createPrevPendingIntent())
                                .setOngoing(true)
                                .addAction(
                                    if (mediaPlayer.isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                                    "play",
                                    createPlayPausePendingIntent()
                                )
                                .addAction(R.drawable.ic_next, "next", createNextPendingIntent())
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setLargeIcon(resource) // set the downloaded image here
                                .build()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    this@MusicPlayerService,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                startForeground(1, notification)
                            }
                        } else {
                            startForeground(1, notification)
                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // no-op
                    }
                })
        }

        private fun getPlaceholderBitmap(): Bitmap {
            return BitmapFactory.decodeResource(resources, R.drawable.ic_play)
        }

        override fun onDestroy() {
            super.onDestroy()
            stopService()
        }

        private fun createPrevPendingIntent(): PendingIntent {
            val intent = Intent(this, MusicPlayerService::class.java).apply {
                action = MusicServiceConst.PREV
            }
            return PendingIntent.getService(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        private fun createPlayPausePendingIntent(): PendingIntent {
            val intent = Intent(this, MusicPlayerService::class.java).apply {
                action = MusicServiceConst.PLAY_PAUSE
            }
            return PendingIntent.getService(
                this, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        private fun createNextPendingIntent(): PendingIntent {
            val intent = Intent(this, MusicPlayerService::class.java).apply {
                action = MusicServiceConst.NEXT
            }
            return PendingIntent.getService(
                this, 2, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        private fun createCancelPendingIntent(): PendingIntent {
            val intent = Intent(this, MusicPlayerService::class.java).apply {
                action = MusicServiceConst.CANCEL
            }
            return PendingIntent.getService(
                this, 3, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }