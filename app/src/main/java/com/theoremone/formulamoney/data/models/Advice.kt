package com.theoremone.formulamoney.data.models

import com.google.gson.annotations.SerializedName

data class Advice(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String
)
