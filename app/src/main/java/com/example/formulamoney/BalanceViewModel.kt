package com.example.formulamoney

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.formulamoney.data.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BalanceViewModel: ViewModel() {
    private val _data = MutableStateFlow("Loading...")
    val data: StateFlow<String> = _data

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://8kq890lk50.execute-api.us-east-1.amazonaws.com/prd/accounts/0172bd23-c0da-47d0-a4e0-53a3ad40828f/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    init {
        viewModelScope.launch {
            loadBalance()
        }
    }

    private suspend fun loadBalance() {
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.getBalance()
            }
            if (response.isSuccessful) {
                _data.value = "$${response.body()?.amount.toString()}"
            }
        } catch (e: Exception) {
            // Handle the error
            Log.e("Tag", "Error while loading API", e)
        }
    }
}