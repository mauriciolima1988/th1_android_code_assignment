package com.theoremone.formulamoney.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.theoremone.formulamoney.data.models.AdviceRequest
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

class AdviceViewModel(private val cachedData: CachedData) : ViewModel() {
    private val _adviceTitle = MutableStateFlow("")
    val adviceTitle: StateFlow<String> = _adviceTitle

    private val _adviceDescription = MutableStateFlow("")
    val adviceDescription: StateFlow<String> = _adviceDescription

    var failedConnection = false

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(
            com.theoremone.formulamoney.data.network.ModifiedOkHttpClient()
                .getUnsafeOkHttpClient()!!.build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(ApiService::class.java)

    init {
        getData()
    }

    private suspend fun loadAdvice() {
        try {
            val adviceResponse = withContext(Dispatchers.IO) {
                apiService.getAdvice(AdviceRequest(transactionIds = arrayListOf(UUID)))
            }
            if (adviceResponse.isSuccessful && adviceResponse.body() != null) {
                val advice = adviceResponse.body()
                _adviceTitle.emit(advice?.title.orEmpty())
                _adviceDescription.emit(advice?.description.orEmpty())
                advice?.let { cachedData.saveAdvice(it) }
            }
            failedConnection = false
        } catch (e: Exception) {
            val cachedAdvice = cachedData.getAdvice()
            _adviceTitle.emit(cachedAdvice?.title.orEmpty())
            _adviceDescription.emit(cachedAdvice?.description.orEmpty())
            failedConnection = true
        }
    }

    fun getData() {
        viewModelScope.launch {
            loadAdvice()
        }
    }

    companion object {
        const val UUID = "0172bd23-c0da-47d0-a4e0-53a3ad40828f"
    }
}