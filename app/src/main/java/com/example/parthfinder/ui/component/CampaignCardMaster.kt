package com.example.parthfinder.ui.component

import RejectRequest
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parthfinder.R
import com.example.parthfinder.api.AuthAPI
import com.example.parthfinder.api.Groups
import com.example.parthfinder.repository.Group
import com.example.parthfinder.repository.Request

@Composable
fun CampaignCardMaster(group:Group, groups: Groups,access: AuthAPI,context: Context) {
    val scrollState = rememberScrollState();
    var name by remember { mutableStateOf(group.name ?: "Nome campagna") }
    var description by remember { mutableStateOf(group.description ?: "Descrizione campagna") }
    val masterName by remember { mutableStateOf(group.master?.username ?: "Master") }
    val id = group.id;
    var modificato by remember { mutableStateOf(false) }
    var acceptDialogVisible by remember { mutableStateOf(false) }
    var rejectDialogVisible by remember { mutableStateOf(false) }
    var deleteDialogVisible by remember { mutableStateOf(false) }

    var selectedRequest by remember { mutableStateOf(Request()) }

    var dialogText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }


    val size = 5;
    if (showDialog) {
        AlertDialog(onDismissRequest = {}, confirmButton = {
            TextButton(onClick = { showDialog = !showDialog }) {
                Text(text = "Ok")
            }
        }, text = {
            Text(
                text = dialogText
            )
        })
    }
    if (acceptDialogVisible) {
        AcceptRequest(onDismiss = { acceptDialogVisible = !acceptDialogVisible },
            groupAPI = groups,
            request = selectedRequest,
            group = group,
            access = access,
            context = context,
            onConcluded = { showDialogLambda, dialogTextLambda ->
                showDialog = showDialogLambda; dialogText = dialogTextLambda; acceptDialogVisible =
                !acceptDialogVisible
            })
    } else if (rejectDialogVisible) {
        RejectRequest(onDismiss = { rejectDialogVisible = !rejectDialogVisible },
            groupAPI = groups,
            request = selectedRequest,
            group = group,
            access = access,
            context = context,
            onConcluded = { showDialogLambda, dialogTextLambda ->
                showDialog = showDialogLambda; dialogText = dialogTextLambda; rejectDialogVisible =
                !rejectDialogVisible
            })
    } else if (deleteDialogVisible) {
        DeleteGroup(onDismiss = { deleteDialogVisible = !deleteDialogVisible },
            groupAPI = groups,
            group = group,
            access = access,
            context = context,
            onConcluded = { showDialogLambda, dialogTextLambda ->
                showDialog = showDialogLambda; dialogText = dialogTextLambda; deleteDialogVisible =
                !deleteDialogVisible
            })
    }
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { deleteDialogVisible = true }) {
                    Icon(
                        modifier = Modifier.fillMaxHeight(0.3f),
                        painter = painterResource(id = R.drawable.bin_icon),
                        contentDescription = "elimina"
                    )
                }


            }
        }
        //HEADER
        Row(
            horizontalArrangement = Arrangement.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            EditableTextBox(
                name,
                "Modifica il nome",
                onTextChange = { newText -> name = newText })
        }
        //BODY
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                EditableTextBox(
                    description,
                    "Modifica la descrizione",
                    onTextChange = { newText -> description = newText })
            }
            //Giocatori
            Column(
                Modifier
                    .padding(40.dp)
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "GIOCATORI", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                if (group.characters!!.isEmpty()) {
                    Text(text = "Nessun giocatore partecipante", Modifier.padding(20.dp))
                } else {
                    for (player in group.characters) {
                        Row(Modifier.padding(20.dp)) {
                            Text(text = "•", modifier = Modifier.padding(end = 8.dp))
                            Text(text = player.username ?: "Giocatore")
                        }
                    }
                }
                if (group.requests!!.isNotEmpty()) {
                    Text(text = "RICHIESTE", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    for (request in group.requests) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(text = "•", modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = request.username ?: "Giocatore",
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Button(
                                    onClick = {
                                        acceptDialogVisible =
                                            !acceptDialogVisible; selectedRequest = request
                                    },
                                    colors = ButtonDefaults.buttonColors(Color.Red),
                                    modifier = Modifier.size(50.dp, 40.dp)
                                ) {
                                    Text(
                                        text = "✔",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Spacer(modifier = Modifier.padding(10.dp))
                                Button(
                                    onClick = {
                                        rejectDialogVisible =
                                            !rejectDialogVisible; selectedRequest = request
                                    },
                                    colors = ButtonDefaults.buttonColors(Color.Black),
                                    modifier = Modifier.size(50.dp, 40.dp)
                                ) {
                                    Text(
                                        text = "✘",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
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
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "CODICE", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Row(Modifier.padding(20.dp)) {
                    Text(text = group.groupCode!!, textDecoration = TextDecoration.Underline)
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
                onClick = {
                    modificato =
                        ModificaCampagna(groups, id!!, name, description, access, context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = "Applica modifiche", color = Color.White);
            }
        }
    }
    if (modificato) {
        ShowSuccessDialog(showDialog = modificato, onDismiss = { modificato = false }) {}
    }
}
@Composable
private fun EditableTextBox(text: String,label:String, onTextChange: (String) -> Unit) {
    TextField(
        value = text,
        onValueChange = { newText -> onTextChange(newText) },
        label = { Text(label) }
    )
}

@Composable
private fun ShowSuccessDialog(showDialog: Boolean, onDismiss: () -> Unit, function: () -> Unit) {
    AlertDialog(
        onDismissRequest = {onDismiss()},
        title = { Text("Success") },
        text = { Text("Modifiche applicate gruppo") },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        }
    )
}


private fun ModificaCampagna(groups: Groups,id:String,name:String,description:String,access:AuthAPI,context: Context):Boolean {
    val groupBody = Group(id = id, name = name, description = description, size = "5")
    val groupAPI = groups.editGroup(groupBody,access, context)
    return !groupAPI.isCompletedExceptionally
}



