package com.ynt.purrytify.data.network

import com.ynt.purrytify.data.model.LoginRequest
import com.ynt.purrytify.data.model.LoginResponse
import com.ynt.purrytify.data.model.ProfileResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface APIService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("/api/profile")
    fun getProfile(@Header("Authorization") authHeader : String):
            Call<ProfileResponse>
}