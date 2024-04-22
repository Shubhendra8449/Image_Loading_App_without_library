package com.imageloadingapp.data.remote.base


import com.google.gson.annotations.SerializedName


data class ErrorModel(
    @field:SerializedName("message")
    val message: String? = "",
    @field:SerializedName("statusCode")
    val statusCode: String? = "",
    @field:SerializedName("type")
    val type: String? = ""
)