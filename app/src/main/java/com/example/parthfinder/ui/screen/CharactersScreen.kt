package com.example.parthfinder.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.parthfinder.api.AuthAPI
import com.example.parthfinder.api.CharacterAPI
import com.example.parthfinder.mokk.mokkCharacter
import com.example.parthfinder.repository.PFCharacter
import com.example.parthfinder.ui.component.CharacterSheet
import com.example.parthfinder.util.imageBitmapFrom
import java.io.ByteArrayOutputStream

@Composable
fun CharactersScreen(context: Context, characters: CharacterAPI, access: AuthAPI) {

  var selectedCharacter by remember { mutableStateOf<PFCharacter?>(null) }
  var characterList by remember {
    mutableStateOf(emptyList<PFCharacter>())
  }
  characters.all(context).thenApply { list ->
    characterList = list ?: emptyList()
    Log.i("CHARACTER", "OK")
  }

  if (selectedCharacter == null) {
    Column() {
      CharacterGrid(characters = characterList) { character -> selectedCharacter = character }
    }
    ExtendedFloatingActionButton(
      modifier = Modifier.background(Color.Blue),
      onClick = {
        selectedCharacter = PFCharacter(
          user = access.getCookiesFromSharedPreferences(context)["name"]!!,
          image = /*getBase64FromDrawable(context, R.drawable.default_image)*/"iVBORw0KGgoAAAANSUhEUgAAAQAAAAEACAIAAADTED8xAAADMElEQVR4nOzVwQnAIBQFQYXff81RUkQCOyDj1YOPnbXWPmeTRef+/3O/OyBjzh3CD95BfqICMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMK0CMO0TAAD//2Anhf4QtqobAAAAAElFTkSuQmCC",
          name = "Premi per modificare il nome",
          characterClass = "Modifica la classe",
          stats = mokkCharacter().stats,
          inventory = emptyList()
        )
        Log.i("Image", selectedCharacter!!.image)
      },
      icon = { Icon(Icons.Filled.Edit, "Extended floating action button.") },
      text = { Text(text = "Nuovo", color = Color.White) }
    )
  } else {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      CharacterSheet(
        character = selectedCharacter!!,
        characters = characters,
        context = context
      ) { selectedCharacter = null }
    }
  }

}

@Composable
fun CharacterGrid(characters: List<PFCharacter>, changeSelection: (PFCharacter) -> Unit) {
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
      Character(it) { character -> changeSelection(character) }
    }
  }
}

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
        bitmap = imageBitmapFrom(character.image),
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

fun getBase64FromDrawable(context: Context, drawableId: Int): String {
  val bitmap = BitmapFactory.decodeResource(context.resources, drawableId)
  val byteArrayOutputStream = ByteArrayOutputStream()
  bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
  val byteArray = byteArrayOutputStream.toByteArray()
  return Base64.encodeToString(byteArray, Base64.DEFAULT)
}
