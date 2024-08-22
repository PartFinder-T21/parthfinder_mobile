package com.example.parthfinder.api

import android.content.Context
import android.util.Log
import com.example.parthfinder.repository.PFCharacter
import com.example.parthfinder.repository.Stat
import com.example.parthfinder.util.toJson
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.concurrent.CompletableFuture

interface Character {

  fun new(character: PFCharacter, context: Context): CompletableFuture<String>
  fun edit(character: PFCharacter, context: Context): CompletableFuture<String>
  fun delete(id: String, context: Context): CompletableFuture<String>
  fun all(context: Context): CompletableFuture<List<PFCharacter>>

}

class CharacterAPI(private val baseUrl: String, private val access: AuthAPI): Character{

  override fun new(character: PFCharacter, context: Context): CompletableFuture<String> {
    val cookies = access.getCookiesFromSharedPreferences(context);
    val characterBody = toJson(character)

    return CompletableFuture
      .supplyAsync{
        Fuel.post("${baseUrl}/character")
          .header(Headers.CONTENT_TYPE, "application/json")
          .header(Headers.COOKIE, "tk=${cookies["tk"]}")
          .body(characterBody)
          .response { _ -> }
          .join()
          .let { response ->
            Log.i("Character", "Received response. Response is $response")
            if (response.statusCode != 201) return@supplyAsync null
            response
          }
          .body().asString("application/json; charset=UTF-8")
          .let { body ->
            JsonParser.parseString(body).asJsonObject.get("data").asJsonObject.get("_id").asString
          }
      }
  }

  override fun edit(character: PFCharacter, context: Context): CompletableFuture<String> {
    val cookies = access.getCookiesFromSharedPreferences(context);
    val characterBody = toJson(character)

    return CompletableFuture
      .supplyAsync{
        Fuel.put("${baseUrl}/character")
          .header(Headers.CONTENT_TYPE, "application/json")
          .header(Headers.COOKIE, "tk=${cookies["tk"]}")
          .body(characterBody)
          .response { _ -> }
          .join()
          .let { response ->
            Log.i("Character", "Received response. Response is $response")
            if (response.statusCode != 200) return@supplyAsync null
            response
          }
          .body().asString("application/json; charset=UTF-8")
          .let { body ->
            JsonParser.parseString(body).asJsonObject.get("data").asJsonObject.get("_id").asString
          }
      }
  }

  override fun delete(id: String, context: Context): CompletableFuture<String> {
    val cookies = access.getCookiesFromSharedPreferences(context);
    val characterBody = toJson(id)

    return CompletableFuture
      .supplyAsync{
        Fuel.delete("${baseUrl}/character")
          .header(Headers.CONTENT_TYPE, "application/json")
          .header(Headers.COOKIE, "tk=${cookies["tk"]}")
          .body(characterBody)
          .response { _ -> }
          .join()
          .let { response ->
            Log.i("Character", "Received response. Response is $response")
            if (response.statusCode != 200) return@supplyAsync null
            id
          }
      }
  }

  override fun all(context: Context): CompletableFuture<List<PFCharacter>> {
    val cookies = access.getCookiesFromSharedPreferences(context);
    val tk = cookies["tk"];

    return CompletableFuture
      .supplyAsync{
        Fuel.delete("${baseUrl}/character")
          .header(Headers.COOKIE, "tk=${cookies["tk"]}")
          .response { _ -> }
          .join()
          .let { response ->
            Log.i("Character", "Received response. Response is $response")
            if (response.statusCode != 200) return@supplyAsync null
            response
          }
          .body().asString("application/json; charset=UTF-8")
          .let { body ->
            JsonParser.parseString(body).asJsonObject.get("data").asJsonArray.map { characterFrom(it.asJsonObject) }
            emptyList()
          }
      }
  }

  fun characterFrom(json: JsonObject): PFCharacter{
    return PFCharacter(
      id = json.get("_id").asString,
      name = json.get("name").asString,
      image = json.get("image").asString,
      characterClass = json.get("class").asString,
      stats = statsFrom(json.get("stats").asJsonArray),
      inventory = json.get("inventory").asJsonArray.map { it.asString },
    )
  }

  fun statsFrom(json: JsonArray): List<Stat> {
      return json.map { stat ->
        Stat(
          stat = stat.asJsonObject.get("stat").asString,
          value = stat.asJsonObject.get("value").asInt,
          )
      }
  }

}

