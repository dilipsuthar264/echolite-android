package com.echolite.app.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.echolite.app.R

@Composable
fun HorizontalAlbumItem(
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
                placeholder = painterResource(R.drawable.ic_play),
                error = painterResource(R.drawable.ic_play),
            ),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Text(
            name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis
        )
    }
}
