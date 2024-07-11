package com.example.parthfinder.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Response
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Access(
    val baseUrl: String
) {

    fun login(context: Context, username: String, password: String): CompletableFuture<String> {
        val jsonBody = JSONObject().apply {
            put("input", username)
            put("password", password)
        }.toString()

        val future = CompletableFuture<String>()

        // Avvio di una coroutine per la chiamata di login
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withTimeout(15000) {
                    Log.i("Login", "Trying to log in $baseUrl/user/login")
                    Log.i("Login", "Body: $jsonBody")

                    val (_, response, result) = Fuel.post("${baseUrl}/user/login")
                        .header(Headers.CONTENT_TYPE, "application/json")
                        .body(jsonBody)
                        .response()

                    Log.i("Login", "Received response. Status code is ${response.statusCode}")

                    if (response.statusCode == 200) {
                        Log.i("Login", "Got $response")
                        saveCookiesToSharedPreferences(context, response)
                        future.complete(result.toString())
                    } else {
                        Log.e("Login", "Error: ${result.component2()?.response}")
                        future.complete("errore")
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("Login", "Timeout during login", e)
                future.complete("-1")
            } catch (e: Exception) {
                Log.e("Login", "Exception during login", e)
                future.completeExceptionally(e)
            }
        }
        return future
    }

    fun saveCookiesToSharedPreferences(context: Context, response: Response) {
        val cookies = response.headers[Headers.SET_COOKIE] ?: return
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        cookies.forEach { cookie ->
            if (cookie.contains("tk=")) {
                val token = cookie.split(";").find { it.startsWith("tk=") }?.split("=")?.get(1)
                token?.let { editor.putString("tk", it) }
            }
            if (cookie.contains("name=")) {
                val name = cookie.split(";").find { it.startsWith("name=") }?.split("=")?.get(1)
                name?.let { editor.putString("name", it) }
            }
            if (cookie.contains("id=")) {
                val id = cookie.split(";").find { it.startsWith("id=") }?.split("=")?.get(1)
                id?.let { editor.putString("id", it) }
            }
        }
        editor.apply()
    }

    fun getCookiesFromSharedPreferences(context: Context): Map<String, String?> {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return mapOf(
            "tk" to sharedPreferences.getString("tk", null),
            "name" to sharedPreferences.getString("name", null),
            "id" to sharedPreferences.getString("id", null)
        )
    }

    fun deleteCookiesFromSharedPreferences(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val cookies = arrayOf("tk","name","id")

        cookies.forEach { cookie ->
            editor.remove(cookie)
        }
        editor.apply()
    }
}