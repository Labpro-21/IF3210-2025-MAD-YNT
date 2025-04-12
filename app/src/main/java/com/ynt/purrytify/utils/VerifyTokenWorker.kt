package com.ynt.purrytify.worker

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ynt.purrytify.MainActivity
import com.ynt.purrytify.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VerifyTokenWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val token = getAccessTokenFromSharedPref(applicationContext)

        return@withContext try {
            val response = RetrofitInstance.api.verifyToken("Bearer $token")

            if (response.code() == 401) {
                val intent = Intent(applicationContext, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
                applicationContext.startActivity(intent)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun getAccessTokenFromSharedPref(context: Context): String {
        val sharedPref = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("access_token", "") ?: ""
    }
}
