package com.echolite.app.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.echolite.app.R

@Composable
fun HorizontalArtistItem(
    name: String,
    image: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp)
    ) {
        Image(
            rememberAsyncImagePainter(
                image,
                placeholder = painterResource(R.drawable.ic_user),
                error = painterResource(R.drawable.ic_user)
            ),
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
        )
        Text(
            name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis
        )
    }
}
