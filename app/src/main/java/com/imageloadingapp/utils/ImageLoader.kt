package com.imageloadingapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.util.LruCache
import android.widget.ImageView
import java.io.File
import java.util.*
import java.util.Collections.synchronizedMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// ImageLoader class for lazy loading and caching images
class ImageLoader private constructor(context: Context) {

    // Maximum cache size based on available memory
    private val maxCacheSize: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8

    // Memory cache for storing bitmaps
    private val memoryCache: LruCache<String, Bitmap>

    // Executor service for loading images asynchronously
    private val executorService: ExecutorService

    // Map to keep track of ImageView and associated image URLs
    private val imageViewMap = synchronizedMap(WeakHashMap<ImageView, String>())

    // Handler for updating UI with loaded images
    private val handler: Handler

    // Disk cache for storing bitmaps persistently
    private val diskCache: DiskCache

    init {
        // Initialize memory cache with maxCacheSize
        memoryCache = object : LruCache<String, Bitmap>(maxCacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // Size of cache item measured in kilobytes
                return bitmap.byteCount / 1024
            }
        }

        // Initialize executor service with fixed thread pool
        executorService = Executors.newFixedThreadPool(5, Utils.ImageThreadFactory())

        // Initialize handler
        handler = Handler()

        // Get screen dimensions
        val metrics = context.resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels

        // Initialize disk cache with cache directory
        diskCache = DiskCache(context.cacheDir)
    }

    companion object {
        // Screen dimensions
        internal var screenWidth = 0
        internal var screenHeight = 0

        // Singleton instance of ImageLoader
        private var INSTANCE: ImageLoader? = null

        // Get singleton instance of ImageLoader
        @Synchronized
        fun with(context: Context): ImageLoader {
            return INSTANCE ?: ImageLoader(context.applicationContext).also {
                INSTANCE = it
            }
        }
    }

    // Load image into ImageView
    fun load(imageView: ImageView, imageUrl: String,placeholderResId: Int?=null) {
        imageView.setImageResource(placeholderResId?:0)
        imageViewMap[imageView] = imageUrl

        val bitmap = checkImageInCache(imageUrl)
        bitmap?.let {
            loadImageIntoImageView(imageView, it, imageUrl)
        } ?: run {
            executorService.submit(PhotosLoader(ImageRequest(imageUrl, imageView)))
        }
    }

    // Load image into ImageView
    private fun loadImageIntoImageView(imageView: ImageView, bitmap: Bitmap?, imageUrl: String) {
        try {
            bitmap?.let {
                val scaledBitmap = Utils.scaleBitmapForLoad(it, imageView.width, imageView.height)
                scaledBitmap?.let {
                    if (!isImageViewReused(ImageRequest(imageUrl, imageView))) {
                        imageView.setImageBitmap(scaledBitmap)
                    }
                }

                // Update memory cache with the loaded bitmap
                memoryCache.put(imageUrl, it)
            } ?: run {
                // If bitmap is null, try to load it from disk cache
                val cachedBitmap = diskCache.getBitmap(imageUrl)
                cachedBitmap?.let {
                    val scaledBitmap = Utils.scaleBitmapForLoad(it, imageView.width, imageView.height)
                    scaledBitmap?.let {
                        if (!isImageViewReused(ImageRequest(imageUrl, imageView))) {
                            imageView.setImageBitmap(scaledBitmap)
                        }
                    }

                    // Update memory cache with the loaded bitmap from disk
                    memoryCache.put(imageUrl, cachedBitmap)
                } ?: run {
                    // If bitmap is still null, load it asynchronously
                    executorService.submit(PhotosLoader(ImageRequest(imageUrl, imageView)))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // Check if ImageView is being reused to avoid redundant loading
    private fun isImageViewReused(imageRequest: ImageRequest): Boolean {
        val tag = imageViewMap[imageRequest.imageView]
        return tag == null || tag != imageRequest.imgUrl
    }

    // Check if image is present in cache (memory or disk) and retrieve it
    @Synchronized
    private fun checkImageInCache(imageUrl: String): Bitmap? {
        var bitmap: Bitmap? = memoryCache.get(imageUrl)
        if (bitmap == null) {
            bitmap = diskCache.getBitmap(imageUrl)
            if (bitmap != null) {
                memoryCache.put(imageUrl, bitmap)
            }
        }
        return bitmap
    }




    // Data class to represent an image request
    inner class ImageRequest(var imgUrl: String, var imageView: ImageView)


    // Runnable to display bitmaps in ImageViews
    inner class DisplayBitmap(private val imageRequest: ImageRequest) : Runnable {
        override fun run() {
            val imageView = imageRequest.imageView
            val bitmap = checkImageInCache(imageRequest.imgUrl)
            bitmap?.let {
                if (!isImageViewReused(imageRequest)) {
                    val scaledBitmap = Utils.scaleBitmapForLoad(it, imageView.width, imageView.height)
                    if (!isImageViewReused(imageRequest)) {
                        imageView.setImageBitmap(scaledBitmap)
                    }
                }
                // Don't recycle the bitmap here to avoid using recycled bitmaps
            }
        }
    }

    // Runnable to load images asynchronously
    inner class PhotosLoader(private val imageRequest: ImageRequest) : Runnable {
        override fun run() {
            if (isImageViewReused(imageRequest)) return

            val bitmap = Utils.downloadBitmapFromURL(imageRequest.imgUrl)
            bitmap?.let {
                memoryCache.put(imageRequest.imgUrl, it)

                if (!isImageViewReused(imageRequest)) {
                    handler.post(DisplayBitmap(imageRequest))
                }
                diskCache.putBitmap(imageRequest.imgUrl, it)
            }
        }
    }
}

// DiskCache class for caching bitmaps to disk
class DiskCache(private val cacheDir: File) {
    // Get bitmap from disk cache
    fun getBitmap(key: String): Bitmap? {
        val file = File(cacheDir, key)
        if (file.exists()) {
            return Utils.decodeBitmapFromFile(file)
        }
        return null
    }

    // Put bitmap into disk cache
    fun putBitmap(key: String, bitmap: Bitmap) {
        val file = File(cacheDir, key)
        Utils.saveBitmapToFile(bitmap, file)
    }
}

