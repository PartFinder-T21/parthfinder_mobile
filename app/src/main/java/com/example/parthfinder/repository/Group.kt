package com.example.parthfinder.repository

data class Group(
    val id: String?= "",
    val groupCode: String?= "",
    val master: Master?= Master(),
    val name: String?= "",
    val description: String?= "",
    val size: String?= "",
    val characters: List<Player>?= emptyList(),
    val requests: List<Player>?= emptyList(),
    val messages: List<Message>?= emptyList(),
)

data class Master(
    val id: String? = "",
    val username: String? = ""
)

data class Player(
    val idUsername: String?= "",
    val username: String?= "",
    val idCharacter: String?= "",
    val character: String?= "",
)

data class Message(
    val idUsername: String?= "",
    val username: String?= "",
    val message: String?= "",
    val isMaster: Boolean?= false,
)