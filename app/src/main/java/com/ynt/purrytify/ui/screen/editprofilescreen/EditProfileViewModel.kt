package com.ynt.purrytify.ui.screen.editprofilescreen

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.auth.SessionManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    var location by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)

    fun editProfile(sessionManager: SessionManager) {
        val uri = imageUri
        val loc = location
        var editedImageFile : MultipartBody.Part? = null
        val context = getApplication<Application>().applicationContext
        if (uri != null) {
            val file = File(getRealPathFromURI(context, uri))
            val requestFile = file.asRequestBody("image/jpeg".toMediaType())
            editedImageFile = MultipartBody.Part.createFormData("profilePhoto", file.name, requestFile)
        }

        val requestCountryCode = loc.toRequestBody("text/plain".toMediaType())
        val editedCountryCode = MultipartBody.Part.createFormData("location", null, requestCountryCode)
        viewModelScope.launch {
            try {
                var response = RetrofitInstance.api.editProfile(
                    authHeader = "Bearer ${sessionManager.getAccessToken()}",
                    location = editedCountryCode,
                    profilePhoto = editedImageFile
                )

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val bodyString = responseBody?.string()
                } else {
                    val errorBody = response.errorBody()
                    val errorString = errorBody?.string()
                    Log.e("EditProfile", "Error: $errorString")
                }

            } catch (e: Exception) {
            }

        }
    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex("_data")
        val filePath = cursor?.getString(columnIndex ?: -1)
        cursor?.close()
        return filePath ?: ""
    }
}