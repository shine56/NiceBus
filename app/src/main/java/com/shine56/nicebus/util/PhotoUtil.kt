package com.shine56.nicebus.util

import android.graphics.Bitmap
import android.graphics.Matrix

object PhotoUtil {
    /**
     * 缩放图片
     * @param bitmap Bitmap
     * @param newWidth Float
     * @param newHeight Float
     * @return Bitmap
     */
    fun zoomPhoto(bitmap: Bitmap, newWidth: Float = 120f, newHeight: Float = 80f): Bitmap {
        val width: Int = bitmap.getWidth()
        val height: Int = bitmap.getHeight()

        val scaleWidth: Float = newWidth / width
        val scaleHeight: Float = newHeight / height

        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)

        val newBitmap= Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)

        return newBitmap
    }
}