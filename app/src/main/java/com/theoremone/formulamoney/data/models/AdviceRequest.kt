package com.theoremone.formulamoney.data.models

import com.google.gson.annotations.SerializedName

data class AdviceRequest(
    @SerializedName("transactionIds") val transactionIds: ArrayList<String> = arrayListOf()
)
