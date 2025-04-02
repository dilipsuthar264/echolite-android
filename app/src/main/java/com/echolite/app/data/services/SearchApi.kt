package com.echolite.app.data.services

import com.echolite.app.data.PaginationModel
import com.echolite.app.data.model.BaseModel
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {

//    @GET("/api/search")
//    suspend fun globalSearch(): Response<BaseModel<>>

    @GET("/api/search/songs")
    suspend fun searchSong(
        @Query("query") query: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<BaseModel<PaginationModel<SongResponseModel>>>

    @GET("/api/search/albums")
    suspend fun searchAlbum(
        @Query("query") query: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<BaseModel<PaginationModel<AlbumResponseModel>>>

    @GET("/api/search/artists")
    suspend fun searchArtist(
        @Query("query") query: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<BaseModel<PaginationModel<ArtistResponseModel>>>
}