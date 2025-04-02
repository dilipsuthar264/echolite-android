package com.echolite.app.data.repository

import com.echolite.app.data.BaseRepository
import com.echolite.app.data.model.BaseModel
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.services.AlbumApi
import javax.inject.Inject

class AlbumRepo @Inject constructor(
    private val albumApi: AlbumApi
) : BaseRepository {

    suspend fun getAlbumById(
        albumId: String
    ): BaseModel<AlbumResponseModel> {
        return handleData(handleResponse { albumApi.getAlbumById(albumId) })
    }

}