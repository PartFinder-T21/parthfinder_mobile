package com.example.parthfinder.repository

import android.media.Image

data class PFCharacter(
  val user: String,
  val image: String,
  val name: String,
  val stats: Stats,
  val inventory: List<String>,
  val charaterClass: String,
)

data class Stats(
  val strength: Int,
  val dexterity: Int,
  val constitution: Int,
  val intelligence: Int,
  val wisdom: Int,
  val charisma: Int,
)
