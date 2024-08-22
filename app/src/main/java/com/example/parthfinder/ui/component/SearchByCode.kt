package com.example.parthfinder.ui.component

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.parthfinder.api.AuthAPI
import com.example.parthfinder.api.CharacterAPI
import com.example.parthfinder.api.Groups
import com.example.parthfinder.repository.PFCharacter

@Composable
fun SearchByCode(groupAPI:Groups,charactersAPI: CharacterAPI,context: Context,access: AuthAPI){
    var code by remember { mutableStateOf("") }
    var options by remember { mutableStateOf( emptyList<PFCharacter>() ) }
    var selectedOption by remember { mutableStateOf("") }
    var showInput by remember { mutableStateOf(true) };

    charactersAPI.all(context).thenAccept {
        if (it != null) {
            options = it
        }
    }


    if(showInput) {
        CodeAndDropdownDialog (code,selectedOption,options,{ showInput = !showInput },
            { newCode ->
                if (newCode.length == 5) {
                    code = newCode;
                }
            },
            { newSelection -> selectedOption = newSelection}
        )
    }
}
@Composable
fun CodeAndDropdownDialog(code:String,selectedOption:String,options:List<PFCharacter>,onDismiss: () -> Unit, onValueChange:(String)->Unit,
                          onSelectOption:(String)->Unit) {
    var newCode by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Inserisci Codice e Seleziona Personaggio") },
        text = {
            Column {
                // Casella di testo per inserire il codice
                TextField(
                    value = newCode,
                    onValueChange = {newValue ->
                        if(newValue.length <=5) {
                            newCode = newValue;
                            onValueChange(newValue)
                        }
                    },
                    label = { Text("Codice (5 caratteri)") },
                    placeholder = { Text(text = "ABCDE")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                // Bottone per mostrare il menu a tendina
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(2.dp,Color.Red),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                ) {
                    Text(if (selectedOption.isEmpty()) "Seleziona un personaggio" else selectedOption)
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.Transparent)
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            colors = MenuDefaults.itemColors(Color.White),
                            modifier = Modifier.background(Color.Transparent),
                            text = {Text("${option.name}-${option.characterClass}")},
                            onClick = {
                                onSelectOption(option.id!!)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (code.length == 5 && selectedOption.isNotEmpty()) {
                Button(
                    colors = ButtonDefaults.buttonColors(Color.Red),
                    onClick = {
                        if (newCode.length == 5 && selectedOption.isNotEmpty()) {
                            //load(selectedOption) // Call the load function with the selected option
                            onDismiss() // Dismiss after selection
                        }
                    }
                ) {
                    Text("Conferma", color = Color.White)
                }
            }
        }
    )
}

