package com.example.parthfinder.ui.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.myapplication.config.Config
import com.example.parthfinder.repository.Group
import com.example.parthfinder.api.Groups
import com.example.parthfinder.repository.Player


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(groups: Groups) {
  var groupList by remember { mutableStateOf(emptyList<Group>()) }
  groups.loadAll().thenAccept { groupList = it }


  Column(
    modifier = Modifier
      .verticalScroll(rememberScrollState())
      .fillMaxWidth()
  ) {
    groupList.forEach {
      GroupCard(it)
    }
  }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GroupCard(group: Group) {
  var visibility by remember { mutableStateOf(CardStatus.Description) }

  Card(
    onClick = {
      visibility = if (visibility == CardStatus.Description) {
        CardStatus.Players
      } else {
        CardStatus.Description
      }
    },
    enabled = (visibility == CardStatus.Description || visibility == CardStatus.Players),
    modifier = Modifier
      .fillMaxWidth()
      .height(250.dp)
      .padding(20.dp)
  ) {
    Scaffold {
      Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight()
          .background(Color(0xFFD9D9D9))
      ) {
        Text(
          text = group.name!!,
          color = Color.White,
          modifier = Modifier
            .background(Color(0xFFFF8000))
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .wrapContentSize(Alignment.Center)
        )
      }

      if (visibility == CardStatus.Join) {
        JoinCard { visibility = CardStatus.Description }
      } else {
        NonJoinCard(visibility = visibility, group = group) {
          visibility = CardStatus.Join
        }
      }
    }

  }
}

@Composable
fun NonJoinCard(visibility: CardStatus, group: Group, onClick: () -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.5f)
        .padding(10.dp),
      colors = CardDefaults.cardColors(
        containerColor = Color(0xFFFFFFFF),
      )
    ) {
      Text(
        text = if (visibility == CardStatus.Description) group.description!! else playerList(group.characters),
        modifier = Modifier
          .padding(10.dp)
      )

    }
  }
  Row(
    verticalAlignment = Alignment.Bottom,
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth(0.5f)
        .fillMaxHeight(0.3f),
      contentAlignment = Alignment.CenterStart

    ) {
      Text(
        text = "${group.characters!!.size}/${group.size} players",
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.padding(20.dp)
      )
    }
    ButtonGroup(onClick, "Gioca")
  }
}

@Composable
fun JoinCard(onClick: () -> Unit) {
  Row(
    verticalAlignment = Alignment.Bottom,
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.4f)
      .padding(10.dp)
  ) {
    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.clickable { onClick() })
  }
  Row(
    verticalAlignment = Alignment.Bottom,
    horizontalArrangement = Arrangement.End,
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight()
  ) {
    ButtonGroup(onCLick = { /*TODO*/ }, text = "Join")
  }
}

@Composable
fun ButtonGroup(onCLick: () -> Unit, text: String){
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.3f),
    contentAlignment = Alignment.CenterEnd
  ) {
    Button(
      onClick = onCLick,
      colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFFCF6060)
      ),
      contentPadding = PaddingValues(0.dp),
      modifier = Modifier
        .padding(15.dp)
    ) {
      Text(
        textAlign = TextAlign.Center,
        modifier = Modifier.wrapContentSize(Alignment.Center),
        text = text,
        fontSize = 2.em
      )
    }
  }
}

enum class CardStatus() {
  Description,
  Players,
  Join
}


fun playerList(characters: List<Player>?): String {
  var text = ""
  characters?.forEach {
    text += "${characters.indexOf(it) + 1}. ${it.character}\n"
  }
  return text
}

