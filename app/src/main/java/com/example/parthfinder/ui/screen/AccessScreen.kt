package com.example.parthfinder.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import com.example.parthfinder.api.AuthAPI


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccessScreen(auth: AuthAPI) {
  val context = LocalContext.current
  var cookies by remember { mutableStateOf(auth.getCookiesFromSharedPreferences(context)) }
  var screen by remember { mutableStateOf(Screen.Login) }

  var alert by remember { mutableStateOf(false) }
  var alertMessage by remember { mutableStateOf("") }

  if (cookies["tk"] != null) {
    screen = Screen.Logout
  }
  if (alert) {
    AccessAlert(message = alertMessage) { alert = false }
  }

  when (screen) {
    Screen.Login -> LoginForm(
      onRegisterClick = { screen = Screen.Register },
      onLoginClick = { username, password ->
        auth.login(context, username, password)
          .thenApply { statusCode ->
            cookies = auth.getCookiesFromSharedPreferences(context)
            alert = true
            alertMessage = when (statusCode) {
              200 -> "Login effettuato con sucesso"
              400 -> "Username o password errati"
              else -> "Errore durante il login, riprova più tardi"
            }
          }
      }
    )

    Screen.Register -> RegisterForm(
      onLoginClick = { screen = Screen.Login },
      onRegisterClick = { username, email, password ->
        auth.register(context, username, email, password)
          .thenApply { statusCode ->
            alert = true
            alertMessage = when (statusCode) {
              201 -> "Registrazione effettuata con sucesso"
              400 -> "Username o password già presenti"
              else -> "Errore durante il login, riprova più tardi"
            }
            screen = Screen.Login
          }
      },
      onError = {
        alert = true
        alertMessage = it }
    )
    Screen.Logout -> LoggedScreen(cookies["name"]?: "") {
      auth.deleteCookiesFromSharedPreferences(context)
      cookies = auth.getCookiesFromSharedPreferences(context)
      screen = Screen.Login
      alert = true
      alertMessage = "LogOut effettuato con sucesso"
    }
  }
}

@Composable
fun LoginForm(onRegisterClick: () -> Unit, onLoginClick: (String, String) -> Unit) {
  var username by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "LOGIN",
      fontSize = 7.em,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(0.dp, 10.dp)
    )

    OutlinedTextField(
      value = username,
      onValueChange = { username = it },
      label = { Text("Username o Email") },
      shape = RoundedCornerShape(30),
      modifier = Modifier.fillMaxWidth(0.7f),
      keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next
      )
    )

    OutlinedTextField(
      value = password,
      onValueChange = { password = it },
      label = { Text("Password") },
      shape = RoundedCornerShape(30),
      visualTransformation = PasswordVisualTransformation(),
      modifier = Modifier.fillMaxWidth(0.7f),
      keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done
      )
    )


    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp, 20.dp),
      horizontalArrangement = Arrangement.Center
    ) {
      Button(onClick = onRegisterClick) {
        Text(text = "Registrati")
      }
      Spacer(modifier = Modifier.fillMaxWidth(0.1f))
      Button(onClick = { onLoginClick(username, password) }) {
        Text(text = "Login")
      }
    }
  }
}

@Composable
fun RegisterForm(onLoginClick: () -> Unit, onRegisterClick: (String, String, String) -> Unit, onError: (String) -> Unit) {
  var username by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") }
  var repeatedPassword by remember { mutableStateOf("") }

  var passwordError by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Text(
      text = "REGISTER",
      fontSize = 7.em,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(0.dp, 10.dp)
    )

    OutlinedTextField(
      value = username,
      onValueChange = { username = it },
      label = { Text("Username") },
      shape = RoundedCornerShape(30),
      modifier = Modifier.fillMaxWidth(0.7f),
      keyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next
      )
    )

    OutlinedTextField(
      value = email,
      onValueChange = { email = it },
      label = { Text("Email") },
      shape = RoundedCornerShape(30),
      modifier = Modifier.fillMaxWidth(0.7f),
      keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Next
      )
    )

    OutlinedTextField(
      value = password,
      onValueChange = {
        password = it
        passwordError = password != repeatedPassword
      },
      label = { Text("Password") },
      shape = RoundedCornerShape(30),
      visualTransformation = PasswordVisualTransformation(),
      isError = passwordError,
      modifier = Modifier.fillMaxWidth(0.7f),
      keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done
      )
    )

    OutlinedTextField(
      value = repeatedPassword,
      onValueChange = {
        repeatedPassword = it
        passwordError = password != repeatedPassword
      },
      label = { Text("Ripeti Password") },
      shape = RoundedCornerShape(30),
      visualTransformation = PasswordVisualTransformation(),
      isError = passwordError,
      modifier = Modifier.fillMaxWidth(0.7f),
      keyboardOptions = KeyboardOptions.Default.copy(
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done
      )
    )


    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(0.dp, 20.dp),
      horizontalArrangement = Arrangement.Center
    ) {
      Button(onClick = onLoginClick) {
        Text(text = "Accedi")
      }
      Spacer(modifier = Modifier.fillMaxWidth(0.1f))
      Button(onClick = {
        if (passwordError) {
          onError("Le password non corrispondono")
        } else if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
          onError("Tutti i campi sono obbligatori")
        }  else {
          onRegisterClick(username, email, password)
        }
      }
      ) {
        Text(text = "Registrati")
      }
    }
  }
}

@Composable
fun LoggedScreen( username: String, onLogoutClick: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ){
    Text(text = "Benvenuto $username")
    Button(onClick = onLogoutClick) {
      Text(text = "LogOut")
    }
  }
}

@Composable
fun AccessAlert(
  message: String,
  close: () -> Unit
) {
  Dialog(onDismissRequest = {}) {
    Card(
      modifier = Modifier
        .wrapContentWidth()
        .wrapContentHeight(),
      shape = RoundedCornerShape(16.dp),
    ) {
      Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = message,
          modifier = Modifier.padding(16.dp),
        )
        Button(
          onClick = close,
          modifier = Modifier.padding(8.dp),
        ) {
          Text("Confirm")
        }
      }
    }
  }
}

enum class Screen {
  Login,
  Register,
  Logout
}