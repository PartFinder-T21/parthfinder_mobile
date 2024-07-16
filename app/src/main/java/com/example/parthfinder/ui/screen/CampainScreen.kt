package com.example.parthfinder.ui.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parthfinder.R
import com.example.parthfinder.api.AuthAPI
import com.example.parthfinder.api.Groups
import com.example.parthfinder.repository.Group

@Composable
fun CampainScreen(groups: Groups,access:AuthAPI,context: Context) {
    var groupList by remember { mutableStateOf(emptyList<Group>()) }
    val my_name = access.getCookiesFromSharedPreferences(context)["id"];
    groups.loadMyGroups(access,context).thenAccept {
        if (it != null) {
            groupList = it
        }
    }
    val masterGroups = groupList.filter { a -> a.master!!.id == my_name }
    val playerGroups = groupList.filter { a -> !masterGroups.contains(a) }
    Log.i("Group",masterGroups.toString())
    Log.i("Group",groups.toString())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Header()
        CampaignSection("Master", masterGroups)
        NewCampaignButton()
        CampaignSection("Giocatore", playerGroups)
        SearchButton()
    }
}

@Composable
fun Header() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(25.dp)
    ) {
        Text(
            text = "Campagne",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CampaignSection(title: String, campaigns: List<Group>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        campaigns.forEach { campaign ->
            CampaignRow(campaign)
        }
    }
}

@Composable
fun CampaignRow(campaign: Group) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFD9D9D9), RoundedCornerShape(12.dp))
            .height(48.dp),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = campaign.name!!,
            color = Color.White,
            fontSize = 16.sp
        )
        Row {
            Button(
                onClick = { /*TODO goto chat*/ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Gioca")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /*TODO open card*/ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Info")
            }
        }
    }
}

@Composable
fun NewCampaignButton() {
    Button(
        onClick = { /*TODO open editor*/ },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Nuova")
    }
}

@Composable
fun SearchButton() {
    Button(
        onClick = { /*TODO open search*/ },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Cerca")
    }
}
