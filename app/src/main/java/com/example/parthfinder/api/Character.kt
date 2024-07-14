package com.example.parthfinder.api

import com.example.parthfinder.repository.PFCharacter
import com.example.parthfinder.util.toJson
import java.util.concurrent.CompletableFuture

interface Character {

  fun new(character: PFCharacter): CompletableFuture<String>
  fun edit(character: PFCharacter): CompletableFuture<String>
  fun delete(id: String): CompletableFuture<String>
  fun all(): CompletableFuture<List<PFCharacter>>

}

class CharacterAPI(): Character{

  override fun new(character: PFCharacter): CompletableFuture<String> {
    val json = toJson(character)




    return CompletableFuture()
  }

  override fun edit(character: PFCharacter): CompletableFuture<String> {
    TODO("Not yet implemented")
  }

  override fun delete(id: String): CompletableFuture<String> {
    TODO("Not yet implemented")
  }

  override fun all(): CompletableFuture<List<PFCharacter>> {
    TODO("Not yet implemented")
  }

}

