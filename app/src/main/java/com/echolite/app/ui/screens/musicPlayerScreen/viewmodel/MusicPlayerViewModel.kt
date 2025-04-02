package com.echolite.app.ui.screens.musicPlayerScreen.viewmodel

import androidx.lifecycle.ViewModel
import com.echolite.app.musicPlayer.MusicPlayerStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewModel @Inject constructor(
    val musicPlayerStateHolder: MusicPlayerStateHolder
) : ViewModel() {
}