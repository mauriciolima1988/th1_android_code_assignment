package com.theoremone.formulamoney.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoremone.formulamoney.data.network.ApiService
import com.theoremone.formulamoney.data.models.TransactionResponse
import com.theoremone.formulamoney.utils.CachedData
import com.theoremone.formulamoney.data.network.ModifiedOkHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TransactionsViewModel(private val cachedData: CachedData): ViewModel() {
    private val _transactions = MutableStateFlow<TransactionResponse?>(null)
    val transactions: StateFlow<TransactionResponse?> = _transactions

    var failedConnection = false

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(ModifiedOkHttpClient().getUnsafeOkHttpClient()!!.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    init {
        getData()
    }

    private suspend fun loadTransactions() {
        try {
            val transactionResponse = withContext(Dispatchers.IO) {
                apiService.getTransactions()
            }
            if (transactionResponse.isSuccessful && transactionResponse.body() != null) {
                _transactions.emit(transactionResponse.body())
                transactionResponse.body()?.let { cachedData.saveTransactions(it) }
            }
            failedConnection = false
        } catch (e: Exception) {
            _transactions.emit(cachedData.getTransactions())
            failedConnection = true
        }
    }

    fun getData() {
        viewModelScope.launch {
            loadTransactions()
        }
    }

    companion object {
        const val baseUrl = "https://8kq890lk50.execute-api.us-east-1.amazonaws.com/prd/accounts/0172bd23-c0da-47d0-a4e0-53a3ad40828f/"
    }
}