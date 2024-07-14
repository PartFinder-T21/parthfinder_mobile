package com.example.parthfinder.api

import android.util.Log
import com.example.parthfinder.repository.Group
import com.example.parthfinder.repository.Master
import com.example.parthfinder.repository.Message
import com.example.parthfinder.repository.Player
import com.github.kittinunf.fuel.Fuel
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.util.concurrent.CompletableFuture

class Groups(
  val baseUrl: String
) {

  fun loadAll(): CompletableFuture<List<Group>> {
    return CompletableFuture
      .supplyAsync{
        Log.i("GroupLoad", "Starting to retretive groups from $baseUrl/group")
        Fuel.get("${baseUrl}/group")
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

  fun mapGroup(json: JsonObject): Group {
    return Group(
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