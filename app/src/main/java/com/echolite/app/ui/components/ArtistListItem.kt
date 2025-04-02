package com.echolite.app.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.echolite.app.R
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.utils.singleClick

@Composable
fun ArtistListItem(artist: ArtistResponseModel?, onClick: (String) -> Unit) {
    CustomListItem(
        title = artist?.name ?: "",
        titleStyle = TextStyle(
            fontSize = 16.sp
        ),
        subtitleStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        leadingContent = {
            AsyncImage(
                model = artist?.image?.lastOrNull()?.url,
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_user),
                error = painterResource(R.drawable.ic_user),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(45.dp)
            )
        },
        onClick = singleClick {
            onClick(artist?.id ?: "")
        }
    )
}