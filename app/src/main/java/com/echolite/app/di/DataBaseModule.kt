package com.echolite.app.di

import android.content.Context
import androidx.room.Room
import com.echolite.app.room.dao.FavoriteSongDao
import com.echolite.app.room.dao.RecentAlbumDao
import com.echolite.app.room.dao.RecentArtistDao
import com.echolite.app.room.dao.RecentSongDao
import com.echolite.app.room.database.EchoLiteDataBase
import com.echolite.app.room.repo.FavoriteSongRepo
import com.echolite.app.room.repo.RecentAlbumRepo
import com.echolite.app.room.repo.RecentArtistRepo
import com.echolite.app.room.repo.RecentSongRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun provideDataBase(
        @ApplicationContext context: Context
    ): EchoLiteDataBase {
        return Room.databaseBuilder(
            context = context,
            klass = EchoLiteDataBase::class.java,
            name = "echo_lite_db"
        )
            .build()
    }


    // fav song

    @Provides
    @Singleton
    fun provideFavoriteSongDao(db: EchoLiteDataBase): FavoriteSongDao {
        return db.favoriteSongDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteSongRepo(favoriteSongDao: FavoriteSongDao): FavoriteSongRepo {
        return FavoriteSongRepo(favoriteSongDao)
    }

    // recent Songs

    @Provides
    @Singleton
    fun provideRecentSongDao(db: EchoLiteDataBase): RecentSongDao {
        return db.recentSongDao()
    }

    @Provides
    @Singleton
    fun provideRecentSongRepo(recentSongDao: RecentSongDao): RecentSongRepo {
        return RecentSongRepo(recentSongDao)
    }

    // recent album

    @Provides
    @Singleton
    fun provideRecentAlbumDao(db: EchoLiteDataBase): RecentAlbumDao {
        return db.recentAlbumDao()
    }

    @Provides
    @Singleton
    fun provideRecentAlbumRepo(recentAlbumDao: RecentAlbumDao): RecentAlbumRepo {
        return RecentAlbumRepo(recentAlbumDao)
    }


    // recent Artist

    @Provides
    @Singleton
    fun provideRecentArtistDao(db: EchoLiteDataBase): RecentAlbumDao {
        return db.recentAlbumDao()
    }

    @Provides
    @Singleton
    fun provideRecentArtistRepo(recentArtistDao: RecentArtistDao): RecentArtistRepo {
        return RecentArtistRepo(recentArtistDao)
    }

}