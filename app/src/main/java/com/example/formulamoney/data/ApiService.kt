package com.example.formulamoney.data

import com.example.formulamoney.data.Balance
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("balance") // Replace with your API endpoint
    suspend fun getBalance(): Response<Balance>
}