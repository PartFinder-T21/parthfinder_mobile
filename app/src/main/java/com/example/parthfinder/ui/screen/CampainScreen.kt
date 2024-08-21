package com.example.parthfinder.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parthfinder.R
import com.example.parthfinder.api.AuthAPI
import com.example.parthfinder.api.Groups
import com.example.parthfinder.repository.Group
import com.example.parthfinder.ui.component.CampaignCardMaster
import com.example.parthfinder.ui.component.NewCampaign

@Composable
fun CampainScreen(context: Context, groups: Groups, access: AuthAPI) {
    var groupList by remember { mutableStateOf(emptyList<Group>()) }
    val my_name = access.getCookiesFromSharedPreferences(context)["id"]
    groups.loadMyGroups(access, context).thenAccept {
        if (it != null) {
            groupList = it
        }
    }
    val masterGroups = groupList.filter { a -> a.master!!.id == my_name }
    val playerGroups = groupList.filter { a -> !masterGroups.contains(a) }

    val scrollState = rememberScrollState()
    var selectedGroup by remember { mutableStateOf<Group?>(null) }
    var newCampaignForm by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(30.dp)
            .verticalScroll(scrollState)
            .fillMaxWidth()
    ) {
        Text(text = "Campagne", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.padding(20.dp))
        Row {
            Icon(
                modifier = Modifier.padding(horizontal = 10.dp),
                painter = painterResource(id = R.drawable.master_icon),
                contentDescription = "home"
            )
            Text(
                text = "Master",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        if(newCampaignForm) {
            NewCampaign(groups = groups, access = access, context = context)
        }
        else {
            masterGroups.forEach { gruppo ->
                CampaignRow(
                    master = true,
                    gruppo = gruppo,
                    context = context,
                    groups = groups,
                    access = access,
                    onCardVisibilityChanged = { group -> selectedGroup = group },
                    isCardVisible = selectedGroup == gruppo
                )
            }
        }

        Button(
            onClick = { newCampaignForm = !newCampaignForm},
            colors = ButtonDefaults.buttonColors(Color.Red),
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Nuova campagna", color = Color.White)
        }

        Spacer(modifier = Modifier.padding(30.dp))
        Row {
            Icon(
                modifier = Modifier.padding(horizontal = 5.dp),
                painter = painterResource(id = R.drawable.player_icon),
                contentDescription = "home"
            )
            Text(
                text = "Giocatore",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
        playerGroups.forEach { gruppo ->
            CampaignRow(
                master = false,
                gruppo = gruppo,
                context = context,
                groups = groups,
                access = access,
                onCardVisibilityChanged = { group -> selectedGroup = group },
                isCardVisible = selectedGroup == gruppo
            )
        }

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(Color.Red),
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "Cerca campagna", color = Color.White)
        }
    }
}

@Composable
fun CampaignRow(
    master: Boolean,
    gruppo: Group,
    context: Context,
    groups: Groups,
    access: AuthAPI,
    onCardVisibilityChanged: (Group) -> Unit,
    isCardVisible: Boolean
) {
    Spacer(Modifier.padding(10.dp))
    Row(
        modifier = Modifier
            .background(Color.LightGray)
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isCardVisible && master) {
            CampaignCardMaster(
                group = gruppo,
                groups = groups,
                access = access,
                context = context
            )
        }
        else {
            Text(
                text = gruppo.name ?: "Nome campagna",
                color = Color.Black,
                fontSize = 10.sp,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text(text = "Gioca", color = Color.White)
                }
                Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                Button(
                    onClick = { onCardVisibilityChanged(gruppo) },
                    colors = ButtonDefaults.buttonColors(Color.Gray)
                ) {
                    Text(text = "Info", color = Color.White)
                }
            }
        }
    }
}
