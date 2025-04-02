package com.echolite.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.echolite.app.R
import com.echolite.app.utils.singleClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    heading: String,
    navController: NavController,
    elevation: Boolean = false,
    isBackNavigation: Boolean = true,
    actions: @Composable() (RowScope.() -> Unit) = {},
    bgColor: Color = MaterialTheme.colorScheme.background
) {
    Column(
//        shadowElevation = if (elevation) 0.5.dp else 0.dp,
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = bgColor
            ),
            title = { Text(heading) },
            navigationIcon = {
                if (isBackNavigation) {
                    AppBarBackBtn(navController, MaterialTheme.colorScheme.onBackground)
                }
            },
            actions = actions,
        )
        if (elevation) {
            HorizontalDivider()
        }
    }
}

@Composable
fun AppBarBackBtn(
    navController: NavController,
    navigationIconTint: Color = MaterialTheme.colorScheme.onBackground,
    size: Dp = Dp.Unspecified
) {
    IconButton(
        onClick = singleClick(onClick = navController::popBackStack),
        modifier = Modifier
            .padding(start = 5.dp)
            .size(size)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = "Back",
            tint = navigationIconTint
        )
    }
}