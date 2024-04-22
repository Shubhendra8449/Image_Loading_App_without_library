package com.imageloadingapp.data.remote.api

// APIService.kt

import com.imageloadingapp.domain.model.ImageModelItem
import com.imageloadingapp.utils.constant.ApiParam
import retrofit2.http.GET

interface APIService {
    @GET(ApiParam.GET_IMAGE)
    suspend fun getImageUrls():ArrayList<ImageModelItem>
}
