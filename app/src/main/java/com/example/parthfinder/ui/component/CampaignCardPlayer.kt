package com.example.parthfinder.ui.component

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parthfinder.api.AuthAPI
import com.example.parthfinder.api.Groups
import com.example.parthfinder.repository.Group
import com.example.parthfinder.repository.Master

@Composable
fun CampaignCardPlayer(group:Group, groups: Groups,access: AuthAPI,context: Context) {
    val scrollState = rememberScrollState();
    var name by remember { mutableStateOf(group.name?:"Nome campagna")}
    var description by remember { mutableStateOf(group.description?:"Descrizione campagna")}
    var masterName by remember { mutableStateOf(group.master?.username?:"Master") }
    val id = group.id;
    val size = 5;
    Card(
        shape = RoundedCornerShape(0.dp), modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
    {
        //CONTAINER
        Column(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .verticalScroll(rememberScrollState())
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //HEADER
            Row(
                horizontalArrangement = Arrangement.Center, modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(20.dp)
            ) {
                Text(text = name, color = Color.Black, fontWeight = FontWeight.Bold)
            }
            //BODY
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.9f)
                    .border(BorderStroke(1.dp, Color.Black))
                    .padding(10.dp),
            )
            {
                //Descrizione
                Column(
                    Modifier
                        .padding(40.dp)
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "DESCRIZIONE", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(text = description, color = Color.Black)
                }
                //Giocatori
                Column(
                    Modifier
                        .padding(40.dp)
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "GIOCATORI", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Row(Modifier.padding(20.dp)) {
                        if(group.characters!!.isEmpty()) {
                            Text(text = "Nessun giocatore partecipante",Modifier.padding(20.dp))
                        }

                        for(player in group.characters) {
                            Text(text = "•", modifier = Modifier.padding(end = 8.dp))
                            Text(text = player.username?:"Giocatore")
                        }
                    }
                }
                //Master
                Column(
                    Modifier
                        .padding(40.dp)
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "MASTER", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Row(Modifier.padding(20.dp)) {
                        Text(text = "•", modifier = Modifier.padding(end = 8.dp))
                        Text(text = masterName, fontWeight = FontWeight.Bold)
                    }
                }
            }

            //FOOTER
            Row(
                Modifier
                    .padding(10.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Gioca", color = Color.White);

                }
            }
        }

    }
}



