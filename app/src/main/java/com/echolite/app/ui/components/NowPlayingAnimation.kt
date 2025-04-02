package com.echolite.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.echolite.app.ui.screens.musicPlayerScreen.viewmodel.MusicPlayerViewModel

@Composable
fun NowPlayingAnimation(
    viewModel: MusicPlayerViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val isPlaying by viewModel.musicPlayerStateHolder.isPlaying.collectAsStateWithLifecycle()
    val infiniteTransition = rememberInfiniteTransition(label = "")

    val barHeights = List(3) {
        infiniteTransition.animateFloat(
            initialValue = 4f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = (300..600).random(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        barHeights.forEach { height ->
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(if (isPlaying) height.value.dp else 4.dp)
                    .background(
                        androidx.compose.material3.MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(50)
                    )
            )
        }
    }
}
