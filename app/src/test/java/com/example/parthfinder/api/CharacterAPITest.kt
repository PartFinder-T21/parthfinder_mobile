package com.example.parthfinder.api

import com.example.parthfinder.mokk.mokkCharacter
import com.example.parthfinder.ui.screen.Character
import org.junit.Test

class CharacterAPITest{
  val character = mokkCharacter()
  val characters = CharacterAPI()

  @Test
  fun test(){
    characters.new(character)
  }



}