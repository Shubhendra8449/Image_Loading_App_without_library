package com.imageloadingapp.data.repository

import com.imageloadingapp.data.remote.api.APIService
import com.imageloadingapp.data.remote.base.ApiState
import com.imageloadingapp.domain.model.ImageModelItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class ImageLoadRepositoryImpl @Inject constructor(
    private val backendApiService: APIService
) : ImageLoadRepository  {

    override suspend fun getAllImages(): Flow<ApiState<ArrayList<ImageModelItem>>> {

            return flow {
                val response = backendApiService.getImageUrls()
                emit(ApiState.success(response))
            }.flowOn(Dispatchers.IO)

    }
}
