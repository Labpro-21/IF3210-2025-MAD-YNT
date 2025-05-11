package com.ynt.purrytify.network

import com.ynt.purrytify.models.LoginRequest
import com.ynt.purrytify.models.LoginResponse
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.models.RefreshTokenRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface APIService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<LoginResponse>

    @GET("/api/profile")
    suspend fun getProfile(@Header("Authorization") authHeader : String): Response<ProfileResponse>

    @GET("/api/verify-token")
    suspend fun verifyToken(@Header("Authorization") authHeader: String): Response<Void>

}