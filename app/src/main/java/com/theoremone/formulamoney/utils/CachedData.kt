package com.theoremone.formulamoney.utils

import android.content.Context
import com.theoremone.formulamoney.data.models.TransactionResponse
import com.google.gson.Gson
import com.theoremone.formulamoney.R
import com.theoremone.formulamoney.data.models.Advice
import com.theoremone.formulamoney.data.models.Balance
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class CachedData(private val context: Context) {
    private val gson = Gson()

    fun saveAdvice(advice: Advice) {
        val file = File(context.cacheDir, ADVICE)
        file.writeText(gson.toJson(advice))
    }

    fun getAdvice(): Advice? {
        val file = File(context.cacheDir, ADVICE)
        return if (file.exists()) {
            val json = file.readText()
            gson.fromJson(json, Advice::class.java)
        } else {
            Gson().fromJson(loadJSONFromRaw(context, R.raw.advice), Advice::class.java)
        }
    }

    fun saveBalance(balance: Balance) {
        val file = File(context.cacheDir, BALANCE)
        file.writeText(gson.toJson(balance))
    }

    fun getBalance(): Balance? {
        val file = File(context.cacheDir, BALANCE)
        return if (file.exists()) {
            val json = file.readText()
            gson.fromJson(json, Balance::class.java)
        } else {
            Gson().fromJson(loadJSONFromRaw(context, R.raw.balance), Balance::class.java)
        }
    }

    fun saveTransactions(transactionResponse: TransactionResponse) {
        val file = File(context.cacheDir, TRANSACTIONS)
        file.writeText(gson.toJson(transactionResponse))
    }

    fun getTransactions(): TransactionResponse? {
        val file = File(context.cacheDir, TRANSACTIONS)
        return if (file.exists()) {
            val json = file.readText()
            gson.fromJson(json, TransactionResponse::class.java)
        } else {
            Gson().fromJson(loadJSONFromRaw(context, R.raw.transactions), TransactionResponse::class.java)
        }
    }

    private fun loadJSONFromRaw(context: Context, resourceId: Int): String? {
        var json: String? = null
        val inputStream: InputStream = context.resources.openRawResource(resourceId)
        try {
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }

    companion object {
        const val ADVICE = "advice.json"
        const val BALANCE = "balance.json"
        const val TRANSACTIONS = "transactions.json"
    }
}
