package com.imageloadingapp.data.remote.interceptor

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnAuthorizedInterceptor @Inject constructor(@ApplicationContext private val context: Context) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().build()


        val response = chain.proceed(request)
        return if (response.code == 403) {
            response
        } else
            response
    }
}