package com.ynt.purrytify.data.network

import com.ynt.purrytify.data.model.LoginRequest
import com.ynt.purrytify.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}