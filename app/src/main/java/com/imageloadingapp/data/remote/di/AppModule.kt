package com.imageloadingapp.data.remote.di

import android.app.Application
import com.google.gson.Gson
import com.imageloadingapp.data.remote.api.APIService
import com.imageloadingapp.utils.constant.AppConstants.BASE_URL
import com.imageloadingapp.data.remote.interceptor.UnAuthorizedInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {


    private val connectTimeUnit = 10L
    private val readTimeUnit = 40L
    private val writeTimeUnit = 40L

    @Provides
    internal fun providesUnAuthorizedInterceptor(application: Application): UnAuthorizedInterceptor =
        UnAuthorizedInterceptor(application)



    @Provides
    @Singleton
    fun provideApiCallServiceUnAuth(unAuthInterceptor: UnAuthorizedInterceptor, cache: Cache, gson: Gson): APIService {

        val okHttpClient =
            OkHttpClient.Builder().cache(cache).connectTimeout(connectTimeUnit, TimeUnit.SECONDS)
                .readTimeout(readTimeUnit, TimeUnit.SECONDS)
                .writeTimeout(writeTimeUnit, TimeUnit.SECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(unAuthInterceptor)


        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson)).client(okHttpClient.build()).build()
            .create(APIService::class.java)

    }

}