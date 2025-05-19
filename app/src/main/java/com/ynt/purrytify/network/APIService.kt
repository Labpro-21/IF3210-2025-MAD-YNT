package com.ynt.purrytify.network

import com.ynt.purrytify.models.LoginRequest
import com.ynt.purrytify.models.LoginResponse
import com.ynt.purrytify.models.OnlineSong
import com.ynt.purrytify.models.ProfileResponse
import com.ynt.purrytify.models.RefreshTokenRequest
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.Locale.IsoCountryCode

interface APIService {
    @POST("/api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/refresh-token")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<LoginResponse>

    @GET("/api/profile")
    suspend fun getProfile(@Header("Authorization") authHeader : String): Response<ProfileResponse>

    @GET("/api/verify-token")
    suspend fun verifyToken(@Header("Authorization") authHeader: String): Response<Void>

    @Multipart
    @PATCH("/api/profile")
    suspend fun editProfile(
        @Header("Authorization") authHeader: String,
        @Part location: MultipartBody.Part?,
        @Part profilePhoto: MultipartBody.Part?
    ): Response<ResponseBody>

    @GET("/api/top-songs/global")
    suspend fun getTopGlobalSongs() : Response<List<OnlineSong>>

    @GET("api/top-songs/{country_code}")
    suspend fun getTopRegionSongs(@Path("country_code") countryCode: String) : Response<List<OnlineSong>>
}