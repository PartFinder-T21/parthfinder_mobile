package com.example.parthfinder.util

import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

@RequiresApi(VERSION_CODES.O)
private val gson = GsonBuilder()
  .registerTypeAdapter(LocalDate::class.java, LocalDateAsJson())
  .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAsJson())
  .create()

@RequiresApi(VERSION_CODES.O)
fun toJson(obj: Any): String = gson.toJson(obj)

@RequiresApi(VERSION_CODES.O)
fun <T> fromJson(json: String, clazz: Class<T>): T = gson.fromJson(json, clazz)

fun <T> fromJson(json: String, type: Type): T = gson.fromJson(json, type)


@RequiresApi(VERSION_CODES.O)
class LocalDateAsJson : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

  override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): LocalDate =
    LocalDate.parse(json.asString, ISO_DATE)
  override fun serialize(src: LocalDate, typeOfSrc: Type, context: JsonSerializationContext) =
    JsonPrimitive(src.format(ISO_DATE))

}

class LocalDateTimeAsJson : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
  override fun deserialize(json: JsonElement, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime =
      LocalDateTime.parse(json.asString, ISO_DATE_TIME)

  override fun serialize(src: LocalDateTime, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement =
      JsonPrimitive(src.format(ISO_DATE_TIME))
}
