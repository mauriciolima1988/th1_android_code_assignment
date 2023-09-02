package com.theoremone.formulamoney.data.network

import com.theoremone.formulamoney.data.models.Advice
import com.theoremone.formulamoney.data.models.AdviceRequest
import com.theoremone.formulamoney.data.models.Balance
import com.theoremone.formulamoney.data.models.TransactionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("balance")
    suspend fun getBalance(): Response<Balance>

    @GET("transactions")
    suspend fun getTransactions(): Response<TransactionResponse>

    @POST("transactions/advice")
    suspend fun getAdvice(@Body adviceRequest: AdviceRequest): Response<Advice>
}