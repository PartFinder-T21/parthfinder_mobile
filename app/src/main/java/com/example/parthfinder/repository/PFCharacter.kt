package com.example.parthfinder.repository

data class PFCharacter(
  val id: String? = null,
  val user: String,
  var image: String,
  var name: String,
  val stats: Stats,
  var inventory: List<String>,
  var characterClass: String,
)

data class Stats(
  var strength: Int,
  var dexterity: Int,
  var constitution: Int,
  var intelligence: Int,
  var wisdom: Int,
  var charisma: Int,
)

data class Stat(
  var stat: String,
  var value: Int
)

