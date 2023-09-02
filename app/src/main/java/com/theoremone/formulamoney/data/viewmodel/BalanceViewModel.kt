package com.theoremone.formulamoney.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoremone.formulamoney.data.network.ApiService
import com.theoremone.formulamoney.utils.CachedData
import com.theoremone.formulamoney.data.viewmodel.Constants.Companion.baseUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BalanceViewModel(private val cachedData: CachedData): ViewModel() {
    private val _balance = MutableStateFlow("Loading...")
    val balance: StateFlow<String> = _balance

    var failedConnection = false

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(com.theoremone.formulamoney.data.network.ModifiedOkHttpClient().getUnsafeOkHttpClient()!!.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    init {
        getData()
    }

    private suspend fun loadBalance() {
        try {
            val balanceResponse = withContext(Dispatchers.IO) {
                apiService.getBalance()
            }
            if (balanceResponse.isSuccessful && balanceResponse.body() != null) {
                val balance = balanceResponse.body()
                _balance.emit("$${balance?.amount}")
                balance?.let { cachedData.saveBalance(it) }
            }
            failedConnection = false
        } catch (e: Exception) {
            _balance.emit("$${cachedData.getBalance()?.amount}")
            failedConnection = true
        }
    }

    fun getData() {
        viewModelScope.launch {
            loadBalance()
        }
    }
}