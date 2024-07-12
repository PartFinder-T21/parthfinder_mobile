package com.example.parthfinder.util

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun base64From(image: ImageBitmap): String{
 return ""
}

fun imageBitmapFrom(base64: String): ImageBitmap{
  return base64.let {
    val encodedImage = Base64.decode(base64, Base64.DEFAULT)
    Log.i("B64", encodedImage.toString())
    BitmapFactory.decodeByteArray(encodedImage, 0, encodedImage.size).asImageBitmap()
  }
}