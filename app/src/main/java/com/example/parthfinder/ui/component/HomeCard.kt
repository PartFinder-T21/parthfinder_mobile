package com.example.parthfinder.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parthfinder.repository.Group
import kotlin.math.round

@Composable
fun HomeCard(group: Group,onPlay:(String)->Unit){
    var showPlayers by remember { mutableStateOf(false) }
    Column(modifier = Modifier
        .fillMaxWidth()
        .height(IntrinsicSize.Max)
        .background(Color.LightGray)) {
        //HEADER
        Text(text = group.name?:"Nome", modifier = Modifier
            .background(Color.Red)
            .padding(12.dp)
            .fillMaxWidth(), color = Color.White, fontSize = 20.sp)
        Row(modifier = Modifier
            .padding(10.dp)
            .height(IntrinsicSize.Max)
            .clip(shape = RoundedCornerShape(10.dp))) {
            //BODY
            if(!showPlayers) {
                Text(
                    text = group.description?:"Descrizione",
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(20.dp),
                    fontSize = 10.sp,
                    color = Color.Black
                )
            }
            else {
                Column (modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(20.dp),)
                {
                    for (player in group.characters!!)
                    Row {
                        Text(text = "•", modifier = Modifier.padding(end = 8.dp), color = Color.Black)
                        Text(
                            text = player.username?:"Giocatore",
                            modifier = Modifier.padding(end = 8.dp),
                            color = Color.Black
                        )
                    }
                }
            }
        }
        //FOOTER
        Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier
            .padding(start = 11.dp, bottom = 2.dp)
            .fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.Start) {
                Text(
                    text = "${group.characters!!.size}/5 giocatori",
                    color = Color.DarkGray,
                    textDecoration = TextDecoration.Underline,
                    fontSize = 10.sp,
                    modifier = Modifier.clickable(onClick = {showPlayers = !showPlayers})
                )
            }
            Row(horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .fillMaxWidth()
                .padding(end = 11.dp)) {
                Button(
                    onClick = {onPlay(group.groupCode!!)},
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    modifier = Modifier.size(height = 20.dp, width = 50.dp).padding(bottom = 2.dp),
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(text = "Gioca", textAlign = TextAlign.Center, fontSize = 10.sp, color = Color.White)
                }
            }
        }

    }
}