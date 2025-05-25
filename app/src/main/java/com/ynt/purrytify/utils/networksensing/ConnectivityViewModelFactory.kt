package com.ynt.purrytify.utils.networksensing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ConnectivityViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val observer = AndroidConnectivityObserver(context.applicationContext)
        return ConnectivityViewModel(observer) as T
    }
}