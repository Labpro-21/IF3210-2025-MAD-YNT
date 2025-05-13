package com.ynt.purrytify.network

import com.ynt.purrytify.models.LoginRequest
import com.ynt.purrytify.models.LoginResponse
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.models.RefreshTokenRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part

interface APIService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<LoginResponse>

    @GET("/api/profile")
    suspend fun getProfile(@Header("Authorization") authHeader : String): Response<ProfileResponse>

    @GET("/api/verify-token")
    suspend fun verifyToken(@Header("Authorization") authHeader: String): Response<Void>

    @PATCH("/api/profile")
    suspend fun editProfile(
        @Header("Authorization") authHeader: String,
        @Part("location") location: MultipartBody.Part?,
        @Part profilePhoto: MultipartBody.Part?
    ): Response<Void>
}