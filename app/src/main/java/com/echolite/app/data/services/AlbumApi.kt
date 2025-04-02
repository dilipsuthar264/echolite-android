package com.echolite.app.data.services

import com.echolite.app.data.model.BaseModel
import com.echolite.app.data.model.response.AlbumResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AlbumApi {

    @GET("/api/albums")
    suspend fun getAlbumById(
        @Query("id") albumId: String? = null
    ): Response<BaseModel<AlbumResponseModel>>
}