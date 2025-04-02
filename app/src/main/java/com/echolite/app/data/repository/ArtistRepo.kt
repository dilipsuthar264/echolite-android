package com.echolite.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.echolite.app.data.BaseRepository
import com.echolite.app.data.model.BaseModel
import com.echolite.app.data.model.QueryModel
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.data.pagginSource.ArtistAlbumPagingSource
import com.echolite.app.data.pagginSource.ArtistSongPagingSource
import com.echolite.app.data.services.ArtistApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class ArtistRepo @Inject constructor(
    private val artistApi: ArtistApi
) : BaseRepository {

    suspend fun getArtistById(
        artistId: String? = null,
        query: QueryModel? = null,
    ): BaseModel<ArtistResponseModel> {
        return handleData(
            handleResponse {
                artistApi.getArtistById(
                    artistId = artistId,
                    songCount = query?.songCount,
                    albumCount = query?.albumCount,
                    sortBy = query?.sortBy,
                    sortOrder = query?.sortOrder
                )
            }
        )
    }


    fun getArtistSongs(
        artistId: String,
        query: QueryModel,
    ): Flow<PagingData<SongResponseModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = query.limit,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                ArtistSongPagingSource(
                    artistApi = artistApi,
                    artistId = artistId,
                    queryModel = query
                )
            }
        ).flow
            .catch {
                emit(PagingData.empty())
            }
    }

    fun getArtistAlbum(
        artistId: String,
        query: QueryModel,
    ): Flow<PagingData<AlbumResponseModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = query.limit,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                ArtistAlbumPagingSource(
                    artistApi = artistApi,
                    artistId = artistId,
                    queryModel = query
                )
            }
        ).flow
            .catch {
                emit(PagingData.empty())
            }
    }
}