package com.echolite.app.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.echolite.app.ui.screens.albumScreen.AlbumScreen
import com.echolite.app.ui.screens.artistsProfileScreen.ArtistAlbumScreen
import com.echolite.app.ui.screens.artistsProfileScreen.ArtistProfileScreen
import com.echolite.app.ui.screens.artistsProfileScreen.ArtistsSongsScreen
import com.echolite.app.ui.screens.dashboard.DashboardScreen
import com.echolite.app.ui.screens.searchScreen.SearchScreen

@Composable
fun NavGraph(navHostController: NavHostController, modifier: Modifier) {

    NavHost(
        modifier = modifier,
        navController = navHostController,
        startDestination = DashboardRoute,
        enterTransition = {
            slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(250)
            ) +
                    fadeIn(tween(250))
        },
        exitTransition = {
            fadeOut(tween(250))
        },
        popEnterTransition = {
            fadeIn(tween(250))
        },
        contentAlignment = Alignment.Center,
        popExitTransition = {
            slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(250)
            ) +
                    fadeOut(tween(250))
        },
    ) {
        composable<DashboardRoute> {
            DashboardScreen(navHostController)
        }
        composable<SearchScreenRoute> {
            SearchScreen(navHostController)
        }
        composable<ArtistProfileScreenRoute> {
            val args = it.toRoute<ArtistProfileScreenRoute>()
            ArtistProfileScreen(navHostController, args)
        }

        composable<ArtistSongsScreenRoute> {
            val args = it.toRoute<ArtistSongsScreenRoute>()
            ArtistsSongsScreen(navHostController, args)
        }

        composable<ArtistAlbumScreenRoute> {
            val args =  it.toRoute<ArtistAlbumScreenRoute>()
            ArtistAlbumScreen(navHostController,args)
        }

        composable<AlbumScreenRoute> {
            val args = it.toRoute<AlbumScreenRoute>()
            AlbumScreen(navHostController, args)
        }
    }
}