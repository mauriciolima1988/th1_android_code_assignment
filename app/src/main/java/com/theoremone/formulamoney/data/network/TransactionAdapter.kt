package com.theoremone.formulamoney.data.network

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theoremone.formulamoney.R
import com.theoremone.formulamoney.data.models.Transaction

class TransactionAdapter(private var transactions: List<com.theoremone.formulamoney.data.models.Transaction>) :
    RecyclerView.Adapter<com.theoremone.formulamoney.data.network.TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(newTransactions: List<Transaction>) {
        this.transactions = newTransactions.takeLast(10)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionAdapter.TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionAdapter.TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.titleTextView.text = transaction.title
        holder.amountTextView.text = transaction.amount.toString()
    }

    override fun getItemCount() = transactions.size
}
