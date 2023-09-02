package com.theoremone.formulamoney.data.models

data class Transaction(
    val title: String,
    val amount: Double,
    val currency: String,
    val id: String
)