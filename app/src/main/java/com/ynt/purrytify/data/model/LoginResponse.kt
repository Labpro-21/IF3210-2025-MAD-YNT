package com.ynt.purrytify.data.model

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)