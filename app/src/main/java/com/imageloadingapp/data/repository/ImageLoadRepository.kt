package com.imageloadingapp.data.repository

import com.imageloadingapp.data.remote.base.ApiState
import com.imageloadingapp.domain.model.ImageModelItem
import kotlinx.coroutines.flow.Flow


interface ImageLoadRepository {
    suspend fun getAllImages(): Flow<ApiState<ArrayList<ImageModelItem>>>
}
