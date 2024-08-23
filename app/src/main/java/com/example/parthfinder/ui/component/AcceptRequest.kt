package com.example.parthfinder.ui.component

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.parthfinder.api.AuthAPI
import com.example.parthfinder.api.Groups
import com.example.parthfinder.repository.Group
import com.example.parthfinder.repository.Request

@Composable
fun AcceptRequest(
    onDismiss: () -> Unit,
    groupAPI: Groups,
    request: Request,
    group: Group,
    access: AuthAPI,
    context: Context,
    onConcluded: (Boolean,String)->Unit
){
    AlertDialog(onDismissRequest = {},
        text = { Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append("Accettare ") }
            append("la richiesta di ${request.username}?")
        })
        },
        confirmButton = {
            Button(
                onClick = {
                    groupAPI.acceptPlayer(group,request,access,context)
                        .thenApply{ result ->
                            // Se la funzione ritorna un risultato valido, imposta il successo
                            if (result == "accettato") {
                                val dialogText = "Operazione riuscita"
                                onConcluded(true,dialogText)
                            }
                            else {
                                val dialogText = "Operazione non riuscita"
                                onConcluded(true,dialogText)

                            }
                        }
                },
                colors = ButtonDefaults.buttonColors(Color.Red)
            )
            {
                Text(text = "Conferma", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.padding(end=50.dp)) {
                Text(text = "Indietro")
            }
        }
    )
}

