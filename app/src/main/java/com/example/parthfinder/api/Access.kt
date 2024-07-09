package com.example.parthfinder.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Response
import com.google.gson.JsonObject
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class Access(
    val baseUrl: String
) {

    fun login(context: Context,username: String, password: String) : CompletableFuture<String> {
        val headers = JSONObject();
        val jsonBody = JSONObject().apply {
            put("input", username)
            put("password", password)
        }.toString()
        headers.put("Content-Type","application/json");
            return CompletableFuture
                .supplyAsync {
                    Log.i("Login", "Trying to log in $baseUrl/user/login")
                    Log.i("Login", "Headers: $headers")
                    Log.i("Login", "Body: $jsonBody")
                    Fuel.post("${baseUrl}/user/login")
                        .header(Headers.CONTENT_TYPE, "application/json")
                        .body(jsonBody)
                        .response { _ -> Unit }
                        .join()
                        .let { response ->
                            Log.i(
                                "Login",
                                "Received response. Status code is ${response.statusCode}"
                            )
                            if (response.statusCode != 200) return@supplyAsync "errore"
                            Log.i(
                                "Login",
                                "Got $response"
                            )
                            saveCookiesToSharedPreferences(context, response)
                            response.toString()
                        }
                }
    }


    fun saveCookiesToSharedPreferences(context: Context, response: Response) {
        val cookies = response.headers[Headers.SET_COOKIE] ?: return
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return mapOf(
            "tk" to sharedPreferences.getString("tk", null),
            "name" to sharedPreferences.getString("name", null),
            "id" to sharedPreferences.getString("id", null)
        )
    }
}