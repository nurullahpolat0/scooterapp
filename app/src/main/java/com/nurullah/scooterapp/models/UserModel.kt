package com.nurullah.scooterapp.models

data class UserModel(
    var uid: String? = "",
    var email: String? = "",
    var password: String? = "",
    var starCount: Int = 0
)

data class Response(
    var users: List<UserModel>? = null,
    var exception: Exception? = null
)