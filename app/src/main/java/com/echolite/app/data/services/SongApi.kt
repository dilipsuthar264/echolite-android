package com.echolite.app.data.services

import com.echolite.app.data.model.BaseModel
import com.echolite.app.data.model.response.SongResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface SongApi {
    @GET("/api/songs/{id}")
    suspend fun findSongById(
        @Path("id") songId: String? = null
    ): Response<BaseModel<List<SongResponseModel>>>
}