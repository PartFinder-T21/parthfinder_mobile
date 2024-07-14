package com.example.parthfinder.repository

data class PFCharacter(
  val id: String? = null,
  val user: String? = null,
  var image: String,
  var name: String,
  val stats: List<Stat>,
  var inventory: List<String>,
  var characterClass: String,
)

data class Stat(
  var stat: String,
  var value: Int
)

