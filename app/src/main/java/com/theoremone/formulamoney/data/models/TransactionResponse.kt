package com.theoremone.formulamoney.data.models

import com.google.gson.annotations.SerializedName

data class TransactionResponse(
    @SerializedName("total") val total: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("data") val data: List<Transaction>
)
