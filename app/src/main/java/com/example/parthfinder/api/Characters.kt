package com.example.parthfinder.api

import android.content.Context
import android.util.Log
import com.example.parthfinder.mokk.mokkCharacter
import com.example.parthfinder.repository.PFCharacter
import com.example.parthfinder.repository.Stat
import com.example.parthfinder.repository.Stats
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.HashMap
import java.util.concurrent.CompletableFuture

class Characters(val baseUrl: String, val access: Access) {

  fun upsert(character: PFCharacter){

  }

  fun newCharacter(character: PFCharacter, context: Context) : CompletableFuture<String> {
    val cookies = access.getCookiesFromSharedPreferences(context);
    val tk = cookies["tk"];
    val future = CompletableFuture<String>();
    if(cookies.all { a -> a.value == null }){
      future.complete("false")
      return future
    }
    val strength = JSONObject().apply {
      put("strength",character.stats.strength)
    }
    val constitution = JSONObject().apply {
      put("constitution",character.stats.constitution)
    }
    val intelligence = JSONObject().apply {
      put("intelligence",character.stats.intelligence)
    }
    val wisdom = JSONObject().apply {
      put("wisdom",character.stats.wisdom)
    }
    val charisma = JSONObject().apply {
      put("charisma",character.stats.charisma)
    }
    val stats = arrayOf(strength,constitution,intelligence,wisdom,charisma)

    val jsonBody = JSONObject().apply {
      put("name", character.name)
      put("image", character.image)
      put("class", character.characterClass)
      put("stats", stats)
      put("inventory",character.inventory.toTypedArray())
    }.toString()
    // Avvio di una coroutine per la chiamata
    CoroutineScope(Dispatchers.IO).launch {
      try {
        withTimeout(15000) {
          Log.i("Character", "Trying to create new character $baseUrl/character")
          Log.i("Character", "Body: $jsonBody")

          val (_, response, result) = Fuel.post("${baseUrl}/character")
            .header(Headers.CONTENT_TYPE, "application/json")
            .header(Headers.AUTHORIZATION, "$tk")
            .body(jsonBody)
            .response()

          Log.i("Character", "Received response. Status code is ${response.statusCode}")

          if (response.statusCode == 201) {
            Log.i("Character", "Got $response")
            future.complete(result.toString())
          } else {
            Log.e("Character", "Error: ${result.component2()?.response}")
            future.complete("errore")
          }
        }
      } catch (e: TimeoutCancellationException) {
        Log.e("Character", "Timeout during login", e)
        future.complete("-1")
      } catch (e: Exception) {
        Log.e("Character", "Exception during login", e)
        future.completeExceptionally(e)
      }
    }
    return future
  }

  fun getCharacters(context: Context) : CompletableFuture<List<PFCharacter>?> {
    val cookies = access.getCookiesFromSharedPreferences(context);
    Log.i("COOKIE", cookies.toString())
    val tk = cookies["tk"];
    val future = CompletableFuture<List<PFCharacter>?>();
    if(cookies.all { a -> a.value == null }){
      future.complete(null)
      return future
    }

    // Avvio di una coroutine per la chiamata
    CoroutineScope(Dispatchers.IO).launch {
      try {
        withTimeout(15000) {
          Log.i("Character", "Trying get character $baseUrl/character")

          val (request, response, result) = Fuel.get("${baseUrl}/character")
            .header(Headers.CONTENT_TYPE, "application/json")
            .header(Headers.COOKIE, "tk=$tk")
            .response()

          Log.i("REQUEST", request.toString())

          Log.i("Character", "Received response. Status code is ${response.statusCode}")

          if (response.statusCode == 200) {
            Log.i("Character", "Got $response")

            //TODO: indeciso sul parsing del body risposta
            val retVal = JsonParser.parseString(String(response.data)).asJsonObject.get("data").asJsonArray.map { mapCharacter(it.asJsonObject) }
            Log.i("CHARACTER",retVal.toString())
            future.complete(retVal)
          } else {
            Log.e("Character", "Error: ${result.component2()?.response}")
            future.complete(null)
          }
        }
      } catch (e: TimeoutCancellationException) {
        Log.e("Character", "Timeout during login", e)
        future.complete(null)
      } catch (e: Exception) {
        Log.e("Character", "Exception during login", e)
        future.completeExceptionally(e)
      }
    }
    return future
  }

  fun editCharacter(character: PFCharacter, context: Context){
    TODO()
  }

  fun mapCharacter(json: JsonObject): PFCharacter {
    val stat = json.get("stats").asJsonArray.map {mapJsonToStat(it.asJsonObject)};
    val inventory = json.get("inventory").asJsonArray.map {it.toString()};

    return PFCharacter(
      id = json.get("_id").asString,
      user = json.get("user").asString,
      image = json.get("image").asString,
      name = json.get("name").asString,
      stats = mapJsonToStats(stat),
      inventory = inventory,
      characterClass = json.get("class").asString
    )
  }
  fun mapJsonToStats(list: List<Stat>): Stats {
    var mappina = HashMap<String,Int>();
    list.forEach { a -> mappina[a.stat] = a.value }
    return Stats(
      strength = mappina["strength"]!!,
      dexterity = mappina["dexterity"]!!,
      constitution = mappina["constitution"]!!,
      intelligence = mappina["intelligence"]!!,
      wisdom = mappina["wisdom"]!!,
      charisma = mappina["charisma"]!!,
    )
  }
  fun mapJsonToStat(json: JsonObject): Stat {
    return Stat(
      stat = json.get("stat").asString,
      value = json.get("value").asInt
    )
  }
}