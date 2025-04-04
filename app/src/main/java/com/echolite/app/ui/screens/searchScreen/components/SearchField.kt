package com.echolite.app.ui.screens.searchScreen.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.room.util.foreignKeyCheck
import com.echolite.app.R
import com.echolite.app.navigation.SearchScreenRoute
import com.echolite.app.ui.components.HorizontalSpace
import com.echolite.app.utils.singleClick

@Composable
fun SearchField(
    focusRequester : FocusRequester,
    searchState: MutableState<String>,
    onSearch: KeyboardActionScope.() -> Unit
) {
    BasicTextField(value = searchState.value,
        onValueChange = {
            if (it.length <= 30) searchState.value = it
        },
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
        textStyle = TextStyle(
            fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .padding(horizontal = 20.dp)
            .border(
                1.dp,
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 14.dp),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = onSearch,
        ),
        decorationBox = { innerBox ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(R.drawable.ic_search_outline),
                    tint = MaterialTheme.colorScheme.onBackground,
                    contentDescription = null,
                )
                HorizontalSpace(10.dp)
                Box() {
                    if (searchState.value.isEmpty()) {
                        Text(
                            stringResource(R.string.search_song_artist_album),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                        )
                    }
                    innerBox()
                }
            }
        })
}