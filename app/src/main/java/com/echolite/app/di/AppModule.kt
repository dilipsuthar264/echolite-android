package com.echolite.app.di

import android.app.Application
import com.echolite.app.BuildConfig
import com.echolite.app.data.services.AlbumApi
import com.echolite.app.data.services.ArtistApi
import com.echolite.app.data.services.SearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    companion object {

        fun getRetrofit(app: Application): Retrofit {
            val httpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(getLoggingInterceptor())
                .build()
            return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build()
        }

        private fun getLoggingInterceptor(): HttpLoggingInterceptor {
            return if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
                    HttpLoggingInterceptor.Logger.DEFAULT.log(message)
                }
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                httpLoggingInterceptor
            } else {
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE }
            }
        }
    }


    @Provides
    @Singleton
    fun provideSearchApi(app: Application): SearchApi {
        return getRetrofit(app).create(SearchApi::class.java)
    }


    @Provides
    @Singleton
    fun provideArtistApi(app: Application): ArtistApi {
        return getRetrofit(app).create(ArtistApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAlbumApi(app: Application): AlbumApi {
        return getRetrofit(app).create(AlbumApi::class.java)
    }
}