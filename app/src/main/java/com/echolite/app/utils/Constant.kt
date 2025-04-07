package com.echolite.app.utils

import androidx.annotation.DrawableRes
import com.echolite.app.R

object NotificationChannelConst {
    const val CHANNEL_ID = "channel_id"
    const val CHANNEL_NAME = "channel_name"
}

object MusicServiceConst {
    const val PREV = "prev"
    const val NEXT = "next"
    const val PLAY_PAUSE = "play_pause"
    const val CANCEL = "cancel"
    const val PLAY = "play"
    const val REPEAT_MODE = "repeat_mode"
    const val SEEK_TO = "seek_to"
}

enum class MusicRepeatMode(
    @DrawableRes val icon:Int
) {
    REPEAT(R.drawable.ic_repeat),
    REPEAT_ONE(R.drawable.ic_repeate_one),
    SHUFFLE(R.drawable.ic_suffle)
}