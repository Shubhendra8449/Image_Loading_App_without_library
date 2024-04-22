package com.imageloadingapp.di

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.imageloadingapp.data.remote.api.APIService
import com.imageloadingapp.data.repository.ImageLoadRepository
import com.imageloadingapp.data.repository.ImageLoadRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RemoteModule {

    /*
   * The method returns the Gson object
   * */
    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    /*
 * The method returns the Cache object
 * */
    @Provides
    @Singleton
    internal fun provideCache(application: Application): Cache {
        val cacheSize = (10 * 1024 * 1024).toLong() // 10 MB
        val httpCacheDirectory = File(application.cacheDir, "http-cache")
        return Cache(httpCacheDirectory, cacheSize)
    }

@Singleton
@Provides
fun provideImageRepository(apiService: APIService): ImageLoadRepository =
    ImageLoadRepositoryImpl(apiService)

}
