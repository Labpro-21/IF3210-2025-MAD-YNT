package com.ynt.purrytify.models

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)