package com.example.parthfinder.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.example.parthfinder.R
import java.io.ByteArrayOutputStream

data class PFCharacter(
  val id: String? = null,
  val user: String? = null,
  var image: String,
  var name: String,
  val stats: List<Stat>,
  var inventory: List<String>,
  var characterClass: String,
){
  companion object{
    fun simplePfCharacter(context: Context): PFCharacter {
      return PFCharacter(
        image = base64FromFile(context, R.drawable.default_image),
        name = "Nome",
        stats =  listOf(
          Stat("strength", 10),
          Stat("dexterity", 10),
          Stat("constitution", 10),
          Stat("intelligence", 10),
          Stat("wisdom", 10),
          Stat("charisma", 10)
        ),
        inventory = emptyList(),
        characterClass = "Classe"
      )
    }
  }
}

data class Stat(
  var stat: String,
  var value: Int
)

fun base64FromFile(context: Context, res: Int): String{
  val byteArrayOutputStream = ByteArrayOutputStream()
  val bitmap = BitmapFactory.decodeResource(context.resources, res)
  bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
  val imageBytes: ByteArray = byteArrayOutputStream.toByteArray()
  Log.i("IMAGE",Base64.encodeToString(imageBytes, Base64.DEFAULT))
  return Base64.encodeToString(imageBytes, Base64.DEFAULT)
}

fun base64FromUri(context: Context, uri: Uri): String {
  val input = context.contentResolver.openInputStream(uri)
  val outputStream = ByteArrayOutputStream()
  BitmapFactory.decodeStream(input, null, null)?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
  val imageBytes = outputStream.toByteArray()
  val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
  return imageString
}

fun imageFrombase64(imageString: String): Bitmap {
  val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
  return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun uriToImageBitmap(context: Context, uri: Uri): Bitmap {
  val inputStream = context.contentResolver.openInputStream(uri)
  val bitmap = BitmapFactory.decodeStream(inputStream)
  return bitmap
}




