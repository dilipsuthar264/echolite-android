package com.echolite.app.musicPlayer.notification

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.echolite.app.MainActivity
import com.echolite.app.R
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.musicPlayer.MusicPlayerService
import com.echolite.app.utils.MusicServiceConst
import com.echolite.app.utils.NotificationChannelConst
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    lateinit var mediaSession: MediaSessionCompat
    private lateinit var service: MusicPlayerService

    fun initialize(service: Service) {
        this.service = service as MusicPlayerService
        mediaSession = MediaSessionCompat(context, "music").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            isActive = true
        }
        mediaSession.setCallback(object : MediaSessionCompat.Callback() {
            override fun onSeekTo(pos: Long) = service.seekTo(pos.toInt())
            override fun onPause() = service.playPause()
            override fun onPlay() = service.playPause()
            override fun onSkipToNext() = service.next()
            override fun onSkipToPrevious() = service.prev()
            override fun onStop() = service.stopService()
        })
    }

    fun sendNotification(track: SongResponseModel, mediaPlayer: MediaPlayer) {
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, track.name)
                .putString(
                    MediaMetadataCompat.METADATA_KEY_ARTIST,
                    track.artists?.primary?.firstOrNull()?.name
                )
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.duration.toLong())
                .build()
        )

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_STOP
            )
            .setState(
                if (mediaPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
                mediaPlayer.currentPosition.toLong(),
                1.0f
            )
            .build()
        mediaSession.setPlaybackState(playbackState)

        val style = androidx.media.app.NotificationCompat.MediaStyle()
            .setShowActionsInCompactView(0, 1, 2)
            .setMediaSession(mediaSession.sessionToken)
            .setShowCancelButton(true)
            .setCancelButtonIntent(createCancelPendingIntent())

        // Load image with Glide and build notification
        val imageUrl = track.image?.last()?.url
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .placeholder(R.drawable.ic_play) // Placeholder while loading
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val notification =
                        buildNotification(track, mediaPlayer.isPlaying, style, resource)
                    startForegroundService(notification)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Fallback if image load fails
                    val placeholderBitmap = getPlaceholderBitmap()
                    val notification =
                        buildNotification(track, mediaPlayer.isPlaying, style, placeholderBitmap)
                    startForegroundService(notification)
                }
            })
    }

    private fun buildNotification(
        track: SongResponseModel,
        isPlaying: Boolean,
        style: androidx.media.app.NotificationCompat.MediaStyle,
        bitmap: Bitmap
    ): Notification {
        return NotificationCompat.Builder(context, NotificationChannelConst.CHANNEL_ID)
            .setStyle(style)
            .setContentTitle(track.name)
            .setContentText(track.artists?.primary?.first()?.name)
            .addAction(R.drawable.ic_previous, "prev", createPrevPendingIntent())
            .addAction(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play,
                "play",
                createPlayPausePendingIntent()
            )
            .addAction(R.drawable.ic_next, "next", createNextPendingIntent())
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(bitmap)
            .setOngoing(true)
            .setContentIntent(createPendingIntent())
            .build()
    }


    private fun startForegroundService(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                service.startForeground(1, notification)
            } else {
                Log.w("MusicNotification", "Notification permission not granted")
            }
        } else {
            service.startForeground(1, notification)
        }
    }

    private fun getPlaceholderBitmap(): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.ic_play)
    }


    private fun createPendingIntent(): PendingIntent {
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        // Create PendingIntent for the notification click
        return PendingIntent.getActivity(
            context,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createPrevPendingIntent(): PendingIntent {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicServiceConst.PREV
        }
        return PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createPlayPausePendingIntent(): PendingIntent {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicServiceConst.PLAY_PAUSE
        }
        return PendingIntent.getService(
            context, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createNextPendingIntent(): PendingIntent {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicServiceConst.NEXT
        }
        return PendingIntent.getService(
            context, 2, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createCancelPendingIntent(): PendingIntent {
        val intent = Intent(context, MusicPlayerService::class.java).apply {
            action = MusicServiceConst.CANCEL
        }
        return PendingIntent.getService(
            context, 3, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun stop() {
        if (::mediaSession.isInitialized) {
            mediaSession.release()
        }
    }
}