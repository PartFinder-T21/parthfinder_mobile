package com.example.parthfinder.api

import android.content.Context
import android.util.Log
import com.example.parthfinder.repository.Group
import com.example.parthfinder.repository.Master
import com.example.parthfinder.repository.Message
import com.example.parthfinder.repository.Player
import com.example.parthfinder.util.toJson
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Groups(
  val baseUrl: String
) {

  fun loadAll(code:String?=""): CompletableFuture<List<Group>> {
    var query = "";
    if(code!!.isNotEmpty()){
      query = "?code=$code"
    }
    return CompletableFuture
      .supplyAsync{
        Log.i("GroupLoad", "Starting to retretive groups from $baseUrl/group")
        Fuel.get("${baseUrl}/group$query")
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

            Log.i("GroupLoad", "Gruppi ${JsonParser.parseString(it).asJsonObject.get("data").asJsonArray.map { mapGroup(it.asJsonObject) }}")
            JsonParser.parseString(it).asJsonObject.get("data").asJsonArray.map { mapGroup(it.asJsonObject) }
          }
      }
  }
  fun loadMyGroups(access:AuthAPI,context: Context): CompletableFuture<List<Group>> {
    val cookies = access.getCookiesFromSharedPreferences(context);
    return CompletableFuture
      .supplyAsync{
        Log.i("MyGroupLoad", "Starting to MY retretive groups from $baseUrl/group")
        Fuel.get("${baseUrl}/group/${cookies["id"]}")
          .header(Headers.CONTENT_TYPE, "application/json")
          .header(Headers.COOKIE, "tk=${cookies["tk"]}")
          .response { _ -> Unit}
          .join()
          .let { response ->
            Log.i("MyGroupLoad", "Received response. Status code is ${response.statusCode}")
            if (response.statusCode != 200) return@supplyAsync null
            response
          }
          .body().asString("application/json; charset=UTF-8")
          .let {
            Log.i("MyGroupLoad", "Got ${JsonParser.parseString(it).asJsonObject.get("data").asJsonArray.size()} groups")
            if (it == "[]") return@supplyAsync null

            Log.i("MyGroupLoad", "Gruppi ${JsonParser.parseString(it).asJsonObject.get("data").asJsonArray.map { mapGroup(it.asJsonObject) }}")
            JsonParser.parseString(it).asJsonObject.get("data").asJsonArray.map { mapGroup(it.asJsonObject) }
          }
      }
  }

  fun createNewGroup(group:Group,access:AuthAPI,context: Context): CompletableFuture<String> {
    val cookies = access.getCookiesFromSharedPreferences(context);
    val groupBody = toJson(group)

    return CompletableFuture
      .supplyAsync{
        Fuel.post("${baseUrl}/group")
          .header(Headers.CONTENT_TYPE, "application/json")
          .header(Headers.COOKIE, "tk=${cookies["tk"]}")
          .body(groupBody)
          .response { _ -> }
          .join()
          .let { response ->
            Log.i("Group", "Received response. Response is $response")
            if (response.statusCode != 201) return@supplyAsync null
            response
          }
          .body().asString("application/json; charset=UTF-8")
          .let {
            "aggiunto"
          }
      }
  }

  fun editGroup(group:Group,access:AuthAPI,context: Context): CompletableFuture<String> {
    val cookies = access.getCookiesFromSharedPreferences(context);
    val jsonBody = JSONObject();
    jsonBody.put("id",group.id);
    jsonBody.put("name",group.name);
    jsonBody.put("description",group.description);
    Log.i("Group",jsonBody.toString())

    return CompletableFuture
      .supplyAsync{
        Fuel.put("${baseUrl}/group")
          .header(Headers.CONTENT_TYPE, "application/json")
          .header(Headers.COOKIE, "tk=${cookies["tk"]}")
          .body(jsonBody.toString())
          .response { _ -> }
          .join()
          .let { response ->
            Log.i("Group", "Received response. Response is $response")
            if (response.statusCode != 200) return@supplyAsync null
            response
          }
          .body().asString("application/json; charset=UTF-8")
          .let {
            "modificato"
          }
      }
  }

  fun mapGroup(json: JsonObject): Group {
    return Group(
      id = json.get("_id").asString?:null,
      groupCode = json.get("code").asString?: null,
      master = mapJsonToMaster(json.get("master").asJsonObject),
      name = json.get("name").asString?: null,
      description = json.get("description").asString?: null,
      size = json.get("size").asInt.toString()?: null,
      characters = json.get("characters").asJsonArray?.map { mapJsonToPlayer(it.asJsonObject) }?: null,
      requests = json.get("requests").asJsonArray?.map { mapJsonToPlayer(it.asJsonObject) }?: null,
      messages = json.get("messages").asJsonArray?.map { mapJsonToMessage(it.asJsonObject) }?: null

    )
  }

  fun mapJsonToMaster(json: JsonObject): Master {
    return Master(
      id = json.get("id").asString?: null,
      username = json.get("username").asString?: null
    )
  }

  fun mapJsonToPlayer(json: JsonObject): Player {
    return Player(
      idUsername = json.get("user").asString?: null,
      username = json.get("username").asString?: null,
      idCharacter = json.get("character").asString?: null
    )
  }

  fun mapJsonToMessage(json: JsonObject): Message {
    return Message(
      username = json.get("user").asString?: null,
      isMaster = json.get("character").asBoolean,
      message = json.get("message").asString?: null
    )
  }
}