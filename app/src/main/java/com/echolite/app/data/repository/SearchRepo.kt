package com.echolite.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.echolite.app.data.BaseRepository
import com.echolite.app.data.model.QueryModel
import com.echolite.app.data.model.response.AlbumResponseModel
import com.echolite.app.data.model.response.ArtistResponseModel
import com.echolite.app.data.model.response.SongResponseModel
import com.echolite.app.data.pagginSource.SearchAlbumPagingSource
import com.echolite.app.data.pagginSource.SearchArtistPagingSource
import com.echolite.app.data.pagginSource.SearchSongPagingSource
import com.echolite.app.data.services.SearchApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class SearchRepo @Inject constructor(
    private val searchApi: SearchApi
) : BaseRepository {

    fun searchSong(
        queryModel: QueryModel
    ): Flow<PagingData<SongResponseModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = queryModel.limit,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                SearchSongPagingSource(
                    searchApi,
                    queryModel
                )
            }
        ).flow
            .catch { e ->
                emit(PagingData.empty())
            }
    }



    fun searchArtist(
        queryModel: QueryModel
    ): Flow<PagingData<ArtistResponseModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = queryModel.limit,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                SearchArtistPagingSource(
                    searchApi,
                    queryModel
                )
            }
        ).flow
            .catch { e ->
                emit(PagingData.empty())
            }
    }


     fun searchAlbum(
        queryModel: QueryModel
    ): Flow<PagingData<AlbumResponseModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = queryModel.limit,
                enablePlaceholders = false,
                prefetchDistance = 5
            ),
            pagingSourceFactory = {
                SearchAlbumPagingSource(
                    searchApi,
                    queryModel
                )
            }
        ).flow
            .catch { e ->
                emit(PagingData.empty())
            }
    }
}