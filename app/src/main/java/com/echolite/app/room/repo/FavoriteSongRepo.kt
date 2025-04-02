package com.echolite.app.room.repo

import com.echolite.app.room.dao.FavoriteSongDao
import com.echolite.app.room.entities.FavoriteSongEntity
import javax.inject.Inject


class FavoriteSongRepo @Inject constructor(
    private val favoriteSongDao: FavoriteSongDao
) {

    suspend fun addSongToFavorite(song: FavoriteSongEntity) {
        favoriteSongDao.addToFavorite(song)
    }

    suspend fun toggleFavorite(song: FavoriteSongEntity) {
        favoriteSongDao.toggleFavorite(song)
    }

    suspend fun removeSongFromFavorite(songId: String) {
        favoriteSongDao.removeFromFavorites(songId)
    }

    suspend fun getFavoriteSongById(songId: String): FavoriteSongEntity? {
        return favoriteSongDao.getFavoriteSongById(songId)
    }

    suspend fun getAllFavorites(): List<FavoriteSongEntity> {
        return favoriteSongDao.getAllFavorites()
    }

    suspend fun isSongFavorite(songId: String): Boolean {
        return favoriteSongDao.isSongFavorite(songId)
    }
}