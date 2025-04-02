package com.echolite.app.navigation

import kotlinx.serialization.Serializable


// Dashboard
@Serializable
object DashboardRoute

@Serializable
object SearchScreenRoute

@Serializable
data class ArtistProfileScreenRoute(
    val artistId: String
)

@Serializable
data class ArtistSongsScreenRoute(
    val artistId: String
)

@Serializable
data class ArtistAlbumScreenRoute(
    val artistId: String
)

@Serializable
data class AlbumScreenRoute(
    val albumId: String
)