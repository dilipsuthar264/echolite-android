package com.echolite.app.data.services

import com.echolite.app.data.PaginationModel
import com.echolite.app.data.model.BaseModel
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ArtistApi {

    @GET("/api/artists/{id}")
    suspend fun getArtistById(
        @Path("id") artistId: String? = null,

        @Query("page") page: Int? = 0,
        @Query("songCount") songCount: Int? = 10,
        @Query("albumCount") albumCount: Int? = 10,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null

    ): Response<BaseModel<ArtistResponseModel>>


    @GET("/api/artists/{id}/songs")
    suspend fun getArtistSongs(
        @Path("id") artistId: String? = null,
        @Query("page") page: Int? = 0,
        @Query("limit") limit: Int? = 10,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null
    ): Response<BaseModel<PaginationModel<SongResponseModel>>>

    @GET("/api/artists/{id}/albums")
    suspend fun getArtistAlbums(
        @Path("id") artistId: String? = null,
        @Query("page") page: Int? = 0,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
        @Query("limit") limit: Int? = 10
    ): Response<BaseModel<PaginationModel<AlbumResponseModel>>>


}