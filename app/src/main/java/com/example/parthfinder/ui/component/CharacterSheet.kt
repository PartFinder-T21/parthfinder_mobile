package com.example.parthfinder.ui.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.parthfinder.R
import com.example.parthfinder.api.CharacterAPI
import com.example.parthfinder.mokk.mokkCharacter
import com.example.parthfinder.repository.PFCharacter
import com.example.parthfinder.repository.Stat
import com.example.parthfinder.repository.base64FromUri
import com.example.parthfinder.repository.imageFrombase64
import com.example.parthfinder.repository.uriToImageBitmap
import java.io.ByteArrayOutputStream

@Composable
fun CharacterSheet(characters: CharacterAPI, character: PFCharacter, context: Context, close: () -> Unit) {
  var sheetState by remember { mutableStateOf(true) }
  var testoCambiaPagina by remember { mutableStateOf("Inventario") }
  Card(
    shape = RoundedCornerShape(0.dp),
    modifier = Modifier
      .fillMaxWidth(0.9f)
      .fillMaxHeight(0.95f)
  ) {
    Icon(Icons.Default.Close, "", Modifier.clickable { close() })
    if (sheetState) {
      CharacterSheetFrontSize(character)
    } else {
      CharacterSheetBackSize(character)
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
      Button(onClick = {
        sheetState = !sheetState
        when (sheetState) {
          true -> testoCambiaPagina = "Inventario"
          false -> testoCambiaPagina = "Stats"
        }
      }) {
        Text(text = testoCambiaPagina)
      }
      Spacer(modifier = Modifier.fillMaxWidth(0.3f))
      Button(onClick = {
        if(character.id == null){
          characters.new(character, context)
        }
        else{
          characters.edit(character, context)
        }
      }) {
        Text(text = "Salva")
      }

    }
  }
}

@Composable
fun CharacterSheetFrontSize(character: PFCharacter) {
  val encodedImage = Base64.decode(character.image, Base64.DEFAULT)
  val context = LocalContext.current
  var imageBitmap by remember {
    mutableStateOf(
      BitmapFactory.decodeByteArray(encodedImage, 0, encodedImage.size)?.asImageBitmap()?: BitmapFactory.decodeResource(context.resources, R.drawable.default_image).asImageBitmap()
    )
  }
  var name by remember { mutableStateOf(character.name) }
  var characterClass by remember { mutableStateOf(character.characterClass) }
  val pickImage =
    rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
      uri?.let {
        val bitmap = uriToImageBitmap(context, it)
        imageBitmap = bitmap.asImageBitmap()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        character.image =  Base64.encodeToString(byteArray, Base64.DEFAULT)
      }
    }
  Column {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp),
      horizontalArrangement = Arrangement.Center
    ) {
      Image(
        bitmap = imageBitmap,
        contentDescription = "Immagine personaggio",
        modifier = Modifier
          .clip(CircleShape)
          .fillMaxWidth(0.5f)
          .aspectRatio(1f)
          .clickable { pickImage.launch("image/*") },
      )
    }
    BasicTextField(
      value = name,
      onValueChange = {
        name = it
        character.name = it
      },
      modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp, 10.dp, 0.dp, 0.dp),
      textStyle = TextStyle(
        textAlign = TextAlign.Center,
        fontSize = 10.em,
        color = Color.White
      ),
      singleLine = true
    )

    BasicTextField(
      value = characterClass,
      onValueChange = {
        characterClass = it
        character.characterClass = it
      },
      modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp, 0.dp, 0.dp, 10.dp),
      textStyle = TextStyle(
        textAlign = TextAlign.Center,
        fontSize = 4.em,
        color = Color.White
      ),
      singleLine = true
    )
    StatsGrid(character)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterSheetBackSize(character: PFCharacter) {
  var inventory by remember { mutableStateOf(inventoryToString(character.inventory)) }
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxSize(0.9f)
  ) {
    Text(
      text = "INVENTARIO",
      textAlign = TextAlign.Center,
      fontWeight = FontWeight.Bold,
      fontSize = 6.em,
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.1f)
        .padding(0.dp, 30.dp, 0.dp, 0.dp)
    )
    TextField(
      value = inventory,
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.9f)
        .padding(20.dp)
        .border(2.dp, Color.Black),
      onValueChange = {
        inventory = it
        character.inventory = inventory.split('\n')
      },

      )
  }
}


@Composable
fun StatsGrid(character: PFCharacter) {
  val keyboardManager = LocalSoftwareKeyboardController.current
  val focusManager = LocalFocusManager.current
  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = Modifier
  ) {
    items(character.stats) {
      Box(
        modifier = Modifier
          .fillMaxWidth(0.5f)
          .padding(10.dp)
          .clickable {
            keyboardManager!!.hide()
            focusManager.clearFocus()
          }
      ) {
       StatBlock(it)
      }
    }
  }
}

@Composable
fun StatBlock(stat: Stat) {
  Row(
    modifier = Modifier.fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    var value by remember { mutableIntStateOf(stat.value) }
    TextField(
      value = value.toString(),
      onValueChange = { number ->
        if(number == "") {
          value = 0
        }
        else { value = number.toInt()}
        stat.value = value
      },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      modifier = Modifier
        .fillMaxWidth(0.5f)
        .border(2.dp, Color.Black, RoundedCornerShape(30)),
      colors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
      ),
      textStyle = TextStyle(
        textAlign = TextAlign.Center,
        fontSize = 5.em,
      ),
    )
    Text(
      text = stat.stat.uppercase(),
      textAlign = TextAlign.Center,
      modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp, 0.dp, 0.dp, 0.dp),
      fontSize = 5.em,
      fontWeight = FontWeight.Bold
    )
  }
}


fun inventoryToString(list: List<String>): String {
  var text = ""
  list.forEach {
    text += "$it\n"
  }
  return text
}