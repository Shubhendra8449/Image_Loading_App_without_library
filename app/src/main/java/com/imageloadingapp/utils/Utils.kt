package com.imageloadingapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Process
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.concurrent.ThreadFactory


// Utils object for utility functions related to bitmap operations
object Utils {
    // Thread factory to set thread priority to background
    internal class ImageThreadFactory : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable).apply {
                name = "ImageLoader Thread"
                priority = Process.THREAD_PRIORITY_BACKGROUND
            }
        }
    }




    // Scale bitmap for loading into ImageView
    fun scaleBitmapForLoad(bitmap: Bitmap, width: Int, height: Int): Bitmap? {
        if (width == 0 || height == 0) return bitmap
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    // Scale bitmap based on specified dimensions
    private fun scaleBitmap(inputStream: BufferedInputStream, width: Int, height: Int): Bitmap? {
        return BitmapFactory.Options().run {
            inputStream.mark(inputStream.available())

            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)

            inSampleSize = calculateInSampleSize(this, width, height)

            inJustDecodeBounds = false
            inputStream.reset()
            BitmapFactory.decodeStream(inputStream, null, this)
        }
    }

    // Calculate inSampleSize for bitmap scaling
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    // Decode bitmap from file
    fun decodeBitmapFromFile(file: File): Bitmap? {
        return BitmapFactory.decodeFile(file.path)
    }

    // Save bitmap to file
    fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
        }
    }

    fun downloadBitmapFromURL(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()
            val inputStream = BufferedInputStream(connection.getInputStream())
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


}
