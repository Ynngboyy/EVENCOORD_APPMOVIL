package com.example.eventcoord.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt


//FUNCION PARA TRANFORMAR IMAGEN A TEXTO
fun uriToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Se reduce la imagen a 400x400 píxeles máximo
        val maxResolution = 400
        val ratio = maxResolution.toFloat() / originalBitmap.width.coerceAtLeast(originalBitmap.height)
        val width = (ratio * originalBitmap.width).roundToInt()
        val height = (ratio * originalBitmap.height).roundToInt()
        val scaledBitmap = originalBitmap.scale(width, height)

        // La imagen se comprime en formato JPEG al 70% de calidad
        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()

        // Se convierte en texto
        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// FUNCION PARA TRANSOFORMAR TEXTO A IMAGEN
fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    return try {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        bitmap.asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}