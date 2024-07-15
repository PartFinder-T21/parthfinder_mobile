package com.example.parthfinder.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream

data class PFCharacter(
  val id: String? = null,
  val user: String? = null,
  var image: String = "",
  var name: String = "Nome",
  val stats: List<Stat> = listOf(
    Stat("strength", 10),
    Stat("dexterity", 10),
    Stat("constitution", 10),
    Stat("intelligence", 10),
    Stat("wisdom", 10),
    Stat("charisma", 10)
  ),
  var inventory: List<String> = emptyList(),
  var characterClass: String = "Classe",
)

data class Stat(
  var stat: String,
  var value: Int
)

fun imageFromFile(context: Context, res: Int): String{
  val byteArrayOutputStream = ByteArrayOutputStream()
  val bitmap = BitmapFactory.decodeResource(context.resources, res)
  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
  val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
  Log.i("IMAGE",Base64.encodeToString(imageBytes, Base64.DEFAULT))
  return Base64.encodeToString(imageBytes, Base64.DEFAULT)
}

fun imageFromUri(context: Context, uri: Uri): String {
  val input = context.contentResolver.openInputStream(uri)
  val outputStream = ByteArrayOutputStream()
  BitmapFactory.decodeStream(input, null, null)?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
  val imageBytes = outputStream.toByteArray()
  val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
  return imageString
}

fun decode(imageString: String): Bitmap {
  val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
  return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}



