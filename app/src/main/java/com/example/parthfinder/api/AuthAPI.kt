package com.example.parthfinder.api

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Headers
import com.github.kittinunf.fuel.core.Response
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

interface Auth {

  fun login(context: Context, username: String, password: String): CompletableFuture<Int>
  fun register(
    context: Context,
    username: String,
    email: String,
    password: String
  ): CompletableFuture<Int>
}

class AuthAPI(private val baseUrl: String) : Auth {

  override fun login(
    context: Context,
    username: String,
    password: String
  ): CompletableFuture<Int> {
    val loginBody = JSONObject().apply {
      put("input", username)
      put("password", password)
    }.toString()

    return CompletableFuture
      .supplyAsync {
        Fuel.post("${baseUrl}/user/login")
          .header(Headers.CONTENT_TYPE, "application/json")
          .body(loginBody)
          .response { _ -> Unit }
          .join()
          .let { response ->
            Log.i("Login", "Received response. Response is $response")
            if (response.statusCode == 200) saveCookiesToSharedPreferences(context, response)
            response.statusCode
          }
      }
  }

  override fun register(
    context: Context,
    username: String,
    email: String,
    password: String
  ): CompletableFuture<Int> {
    val registerBody = JSONObject().apply {
      put("username", username)
      put("email", email)
      put("password", password)
    }.toString()

    return CompletableFuture
      .supplyAsync {
        Fuel.post("${baseUrl}/user/register")
          .header(Headers.CONTENT_TYPE, "application/json")
          .body(registerBody)
          .response { _ -> Unit }
          .join()
          .let { response ->
            Log.i("Register", "Received response. Response is $response")
            if (response.statusCode == 200) saveCookiesToSharedPreferences(context, response)
            response.statusCode
          }
      }
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
    val cookies = arrayOf("tk", "name", "id")

    cookies.forEach { cookie ->
      editor.remove(cookie)
    }
    editor.apply()
  }
}