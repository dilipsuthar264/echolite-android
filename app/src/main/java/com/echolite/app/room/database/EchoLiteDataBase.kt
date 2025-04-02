package com.echolite.app.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.echolite.app.room.dao.FavoriteSongDao
import com.echolite.app.room.dao.RecentAlbumDao
import com.echolite.app.room.dao.RecentArtistDao
import com.echolite.app.room.dao.RecentSongDao
import com.echolite.app.room.entities.AlbumEntity
import com.echolite.app.room.entities.ArtistEntity
import com.echolite.app.room.entities.FavoriteSongEntity
import com.echolite.app.room.entities.SongEntity
import com.echolite.app.room.typeConverter.Converters


@Database(
    entities = [SongEntity::class, AlbumEntity::class, ArtistEntity::class, FavoriteSongEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class EchoLiteDataBase : RoomDatabase() {

    abstract fun favoriteSongDao(): FavoriteSongDao

    abstract fun recentSongDao(): RecentSongDao

    abstract fun recentAlbumDao(): RecentAlbumDao

    abstract fun recentArtistDao(): RecentArtistDao
}