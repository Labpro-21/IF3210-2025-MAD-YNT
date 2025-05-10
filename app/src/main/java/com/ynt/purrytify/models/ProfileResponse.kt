package com.ynt.purrytify.models

import android.media.Image
import java.util.Date

data class ProfileResponse(
    val id: Int,
    val username: String,
    val email: String,
    val profilePhoto: String,
    val location: String,
    val createdAt: Date,
    val updatedAt: Date
) {
    val photoURL: String
        get() = "https://34.101.226.132:3000/uploads/profile-picture/$profilePhoto"
}