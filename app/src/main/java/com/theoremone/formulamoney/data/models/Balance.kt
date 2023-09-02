package com.theoremone.formulamoney.data.models

import com.google.gson.annotations.SerializedName

data class Balance (
    @SerializedName("amount") val amount: Double,
    @SerializedName("currency") val currency: String
)
