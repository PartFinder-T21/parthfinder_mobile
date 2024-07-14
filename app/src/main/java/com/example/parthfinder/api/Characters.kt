package com.example.parthfinder.api

import android.content.Context
import android.util.Log
import com.example.parthfinder.mokk.mokkCharacter
import com.example.parthfinder.repository.PFCharacter
import com.example.parthfinder.repository.Stat
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
import java.util.concurrent.CompletableFuture

class Characters(val baseUrl: String, val access: AuthAPI) {

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
    /*val jsonStats = JSONArray()
      .put(JSONObject().put("stat", "strength").put("value", character.stats.strength))
      .put(JSONObject().put("stat", "dexterity").put("value", character.stats.dexterity))
      .put(JSONObject().put("stat", "constitution").put("value", character.stats.constitution))
      .put(JSONObject().put("stat", "intelligence").put("value", character.stats.intelligence))
      .put(JSONObject().put("stat", "wisdom").put("value", character.stats.wisdom))
      .put(JSONObject().put("stat", "charisma").put("value", character.stats.charisma))*/

    val jsonBody = JSONObject().apply {
      put("name", character.name)
      put("image", character.image)
      put("class", character.characterClass)
      //put("stats", jsonStats)
      put("inventory",character.inventory)
    }.toString()
    // Avvio di una coroutine per la chiamata
    CoroutineScope(Dispatchers.IO).launch {
      try {
        withTimeout(15000) {
          Log.i("Character", "Trying to create new character $baseUrl/character")
          Log.i("Character", "Body: $jsonBody")

          val (_, response, result) = Fuel.post("${baseUrl}/character")
            .header(Headers.CONTENT_TYPE, "application/json")
            .header(Headers.COOKIE, "tk=$tk")
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

  /*fun editCharacter(character: PFCharacter, context: Context): CompletableFuture<Unit>{
    val token  = access.getCookiesFromSharedPreferences(context);
    return CompletableFuture
      .supplyAsync {
        Fuel.put("${baseUrl}/character")
          .header(Headers.CONTENT_TYPE, "application/json")
          .header(Headers.COOKIE, "tk=${token["tk"]}")
          .response()
          .response { _ -> Unit}
          .join()
          .let { response ->
            Log.i("GroupLoad", "Received response. Status code is ${response.statusCode}")
            if (response.statusCode != 200) return@supplyAsync null
            response
          }
          .body().asString("application/json; charset=UTF-8")
          .let {
            Log.i("GroupLoad", "Got ${JsonParser.parseString(it).asJsonObject.get("data").asJsonArray.size()} groups")
            if (it == "[]") return@supplyAsync null

            JsonParser.parseString(it).asJsonObject.get("data").asJsonArray.map { mapGroup(it.asJsonObject) }
          }
      }
  }*/


  fun mapCharacter(json: JsonObject): PFCharacter {
    val stat = json.get("stats").asJsonArray.map {mapJsonToStat(it.asJsonObject)};
    val inventory = json.get("inventory").asJsonArray.map {it.toString()};

    return PFCharacter(
      id = json.get("_id").asString,
      user = json.get("user").asString,
      image = json.get("image").asString,
      name = json.get("name").asString,
      stats = mokkCharacter().stats,
      inventory = inventory,
      characterClass = json.get("class").asString
    )
  }
  /*fun mapJsonToStats(list: List<Stat>): Stat {
    var mappina = HashMap<String,Int>();
    list.forEach { a -> mappina[a.stat] = a.value }
    return Stat(
      strength = mappina["strength"]!!,
      dexterity = mappina["dexterity"]!!,
      constitution = mappina["constitution"]!!,
      intelligence = mappina["intelligence"]!!,
      wisdom = mappina["wisdom"]!!,
      charisma = mappina["charisma"]!!,
    )
  }*/
  fun mapJsonToStat(json: JsonObject): Stat {
    return Stat(
      stat = json.get("stat").asString,
      value = json.get("value").asInt
    )
  }
}