package com.example.parthfinder.ui.screen

import android.content.Context
import android.widget.Space
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.parthfinder.api.AuthAPI
import com.example.parthfinder.api.CharacterAPI
import com.example.parthfinder.api.Groups
import com.example.parthfinder.repository.Group
import com.example.parthfinder.ui.component.HomeCard
import com.example.parthfinder.ui.component.SearchByCode

@Composable
fun Home(groupAPI:Groups, charactersAPI: CharacterAPI, access:AuthAPI, context:Context) {
    var groupList by remember { mutableStateOf(emptyList<Group>()) }
    var showJoinForm by remember { mutableStateOf(false) }
    var selecterGroup by remember { mutableStateOf("") }
    groupAPI.loadAll().thenAccept { groupList = it }

    Column(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .padding(10.dp)) {
        for (group in groupList) {
            HomeCard(
                group = group,
                onPlay = { code -> selecterGroup = code; showJoinForm = !showJoinForm }
            )
            Spacer(modifier = Modifier.padding(bottom = 40.dp))
        }
    }
    if(showJoinForm) {
        SearchByCode(groupAPI = groupAPI, charactersAPI = charactersAPI, context = context, access = access, selectedCode = selecterGroup)
    }
}