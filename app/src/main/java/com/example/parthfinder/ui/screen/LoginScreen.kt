package com.example.parthfinder.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.parthfinder.api.Access
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun LoginScreen(access: Access) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var dialogLogin by remember { mutableStateOf(false) }
    val responseState = remember { mutableStateOf<String>("") }
    val messageState = remember { mutableStateOf<String>("") }
    val context = LocalContext.current;
    var isLoading by remember { mutableStateOf(false) }
    var dialogCredenziali by remember { mutableStateOf(false) }
    var dialogTimeout by remember { mutableStateOf(false) }
    var logout by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var cookies by remember { mutableStateOf(access.getCookiesFromSharedPreferences(context)) }


    if (cookies.all { a -> a.value != null }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Benvenuto, ${cookies["name"]}!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    logout = true;
                },
            ) {
                Text(text = "Logout")
            }
        }

        if(logout) {
            com.example.parthfinder.ui.screen.logout(access = access)
            cookies = access.getCookiesFromSharedPreferences(context);

        }

    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Username o Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        errorMessage = "Per favore, inserisci email e password"
                    } else {
                        isLoading = true
                        access.login(context, email, password)
                            .whenComplete { result, exception ->
                                isLoading = false
                                if (exception != null) {
                                    errorMessage = "Errore durante il login: ${exception.message}"
                                } else {
                                    // Gestisci il risultato
                                    if (result == "errore") {
                                        dialogCredenziali = true // Mostra il dialogo in caso di errore
                                    } else if (result == "-1") {
                                        dialogTimeout = true
                                    }
                                    else {
                                        dialogLogin = true // Imposta il flag di successo
                                    }
                                }
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Login")
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 16.dp)
                )
            }

            if (dialogCredenziali) {
                AlertDialog(
                    onDismissRequest = { dialogCredenziali = false },
                    title = { Text("Errore di login") },
                    text = { Text("Credenziali non valide. Riprova.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                dialogCredenziali = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
            if (dialogTimeout) {
                AlertDialog(
                    onDismissRequest = { dialogTimeout = false },
                    title = { Text("Request timeout") },
                    text = { Text("Richiesta ha raggiunto il timeout") },
                    confirmButton = {
                        Button(
                            onClick = {
                                dialogTimeout = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
            if (dialogLogin) {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text("Login") },
                    text = { Text("Login avvenuto con successo") },
                    confirmButton = {
                        Button(
                            onClick = {
                                dialogLogin = false
                            }
                        ) {
                            Text("OK")
                        }
                    }
                )
            }
            Surface(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    Text(
                        text = responseState.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = messageState.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
@Composable
fun logout(access: Access) {
    access.deleteCookiesFromSharedPreferences(LocalContext.current)
}
