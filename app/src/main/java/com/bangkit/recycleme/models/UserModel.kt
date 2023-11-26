package com.bangkit.recycleme.models

data class UserModel(
    val email: String,
    var token: String,
    val isLogin: Boolean = false
)