package com.imageloadingapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.gson.Gson
import com.imageloadingapp.ImageShowApp
import com.imageloadingapp.data.remote.base.ErrorModel
import com.imageloadingapp.utils.constant.AppConstants
import retrofit2.HttpException

object AppUtils {
    fun errorFormatter(catch: Throwable?): ErrorModel {
        return try {
            val errorResponse = (catch as? HttpException)?.response()?.errorBody()?.string()
            if (errorResponse == null) {
                println("errorFormatter================" + catch?.message)
                println("errorFormatter================" + catch?.localizedMessage)
            }
            Gson().fromJson(errorResponse, ErrorModel::class.java)
        } catch (e: Exception) {
            ErrorModel()
        }

    }

    fun apiError(string: String): String {
        return if (string.isEmpty()) {
            if (isNetworkAvailable()) {
                (AppConstants.SOMETHING_WENT_WRONG)
            } else AppConstants.NO_INTERNET
        } else (string)

    }
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            ImageShowApp.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> {

                return false

            }
        }
    }
}