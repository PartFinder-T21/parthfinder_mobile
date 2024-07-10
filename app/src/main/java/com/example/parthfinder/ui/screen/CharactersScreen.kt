package com.example.parthfinder.ui.screen

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.parthfinder.mokk.mokkCharacter
import com.example.parthfinder.repository.PFCharacter
import com.example.parthfinder.repository.Stats
import com.example.parthfinder.ui.component.CharacterSheet
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun CharactersScreen() {
  val characters = listOf(
    mokkCharacter()
  )
  Column() {
    CharacterGrid(characters = characters)
  }

}


@Composable
fun CharacterGrid(characters: List<PFCharacter>) {
  var selectedCharacter by remember { mutableStateOf<PFCharacter?>(null) }

  if (selectedCharacter == null) {
    Text(
      text = "PERSONAGGI",
      textAlign = TextAlign.Center,
      textDecoration = TextDecoration.Underline,
      fontSize = 8.em,
      modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp, 20.dp, 0.dp, 0.dp)
    )
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      horizontalArrangement = Arrangement.Center,
      verticalArrangement = Arrangement.Top,
      modifier = Modifier
        .padding(20.dp)
        .fillMaxSize()
    ) {
      items(characters) {
        Character(it) { character -> selectedCharacter = character }
      }
    }
  } else {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      CharacterSheet(character = selectedCharacter!!) {selectedCharacter = null}
    }
  }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun Character(character: PFCharacter, onClick: (PFCharacter) -> Unit) {
  Box(
    modifier = Modifier
      .padding(10.dp)
      .fillMaxWidth(0.33f)
      .height(110.dp)
      .clickable(onClick = { onClick(character) }),
    contentAlignment = Alignment.Center,
  ) {
    Column {
      Image(
        bitmap = character.image.let {
          val encodedImage = Base64.decode(character.image, Base64.DEFAULT)
          Log.i("B64", encodedImage.toString())
          BitmapFactory.decodeByteArray(encodedImage, 0, encodedImage.size).asImageBitmap()
        },
        contentDescription = "Immagine personaggio",
        modifier = Modifier.clip(CircleShape)
      )
      Text(
        text = character.name,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxSize()
      )
    }
  }
}

