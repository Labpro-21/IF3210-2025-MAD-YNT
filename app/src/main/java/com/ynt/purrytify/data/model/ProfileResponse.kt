package com.ynt.purrytify.data.model

import android.media.Image
import java.util.Date

data class ProfileResponse(
    val id: Int,
    val username: String,
    val email: String,
    val profilePhoto: String, // photo url
    val location: String, // country code negara
    val createdAt: Date,
    val updatedAt: Date
) {
    val photoURL: String
        get() = "http://34.101.226.132:3000/uploads/profile-picture/$profilePhoto"
}