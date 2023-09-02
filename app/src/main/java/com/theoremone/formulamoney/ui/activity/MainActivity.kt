package com.theoremone.formulamoney.ui.activity

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theoremone.formulamoney.R
import com.theoremone.formulamoney.data.viewmodel.BalanceViewModel
import com.theoremone.formulamoney.data.network.TransactionAdapter
import com.theoremone.formulamoney.data.viewmodel.AdviceViewModel
import com.theoremone.formulamoney.data.viewmodel.TransactionsViewModel
import com.theoremone.formulamoney.utils.CachedData
import com.theoremone.formulamoney.utils.NetworkChecker
import com.theoremone.formulamoney.utils.NetworkStateListener
import com.theoremone.formulamoney.utils.billing.SubscriptionMainManager
import com.theoremone.formulamoney.utils.billing.SubscriptionManagerFactory
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : ComponentActivity(), NetworkStateListener, PremiumSubscriptionContract {
    private lateinit var balanceViewModel: BalanceViewModel
    private lateinit var transactionViewModel: TransactionsViewModel
    private lateinit var adviceViewModel: AdviceViewModel
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var rvTransactions: RecyclerView
    private lateinit var networkReceiver: NetworkChecker
    private lateinit var noteButton: CardView
    private lateinit var ivIcon: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private val subscriptionManager: SubscriptionMainManager by viewModels {
        SubscriptionManagerFactory(this.application, this)
    }
    private var fakeSubscriptionTestOnOff = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNetworkChecker()
        setupViewModels()
        setBottomCard()
        setupExpensesList()
        setSubscriptionListener()
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }

    override fun onNetworkAvailable() {
        if (balanceViewModel.failedConnection) {
            balanceViewModel.getData()
        }
        if (transactionViewModel.failedConnection) {
            transactionViewModel.getData()
        }
        if (subscriptionManager.isSubscribed.value == true && adviceViewModel.failedConnection) {
            adviceViewModel.getData()
        }
    }

    override fun premiumOn() {
        noteButton.setCardBackgroundColor(resources.getColor(R.color.premium_on))
        ivIcon.setImageDrawable(
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.baseline_check_circle_24
            )
        )
        Toast.makeText(applicationContext, getText(R.string.premium_turned_on), Toast.LENGTH_SHORT)
            .show()
        loadPremiumAdvice()
    }

    override fun premiumOff() {
        noteButton.setCardBackgroundColor(resources.getColor(R.color.premium_off))
        tvTitle.setText(R.string.premium_offer_title)
        tvDescription.setText(R.string.premium_offer_description)
        ivIcon.setImageDrawable(
            AppCompatResources.getDrawable(
                applicationContext,
                R.drawable.baseline_info_24
            )
        )
    }

    private fun setupNetworkChecker() {
        networkReceiver = NetworkChecker(this)
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, intentFilter)
    }

    private fun setupViewModels() {
        val cachedData = CachedData(applicationContext)

        balanceViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BalanceViewModel(cachedData) as T
            }
        })[BalanceViewModel::class.java]

        transactionViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TransactionsViewModel(cachedData) as T
            }
        })[TransactionsViewModel::class.java]

        adviceViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdviceViewModel(cachedData) as T
            }
        })[AdviceViewModel::class.java]
    }

    private fun setBottomCard() {
        noteButton = findViewById(R.id.noteButton)
        noteButton.setOnClickListener {
            if (!fakeSubscriptionTestOnOff) {
                fakeSubscriptionTestOnOff = true
                subscriptionManager.startSubscriptionFlow()
            } else {
                fakeSubscriptionTestOnOff = false
                subscriptionManager.isSubscribed.value = false
                subscriptionManager.isTheoremOneSubscriptionTestStarted.value = false
                subscriptionManager.checkSubscriptionStatus()
                Toast.makeText(
                    applicationContext,
                    getText(R.string.premium_turned_off),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        ivIcon = findViewById(R.id.ivIcon)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
    }

    private fun setupExpensesList() {
        rvTransactions = findViewById(R.id.rvTransactions)
        rvTransactions.layoutManager = LinearLayoutManager(this)
        transactionAdapter = TransactionAdapter(listOf())
        rvTransactions.adapter = transactionAdapter
    }

    private fun setSubscriptionListener() {
        subscriptionManager.isSubscribed.observe(this) { isSubscribed ->
            if (isSubscribed) {
                premiumOn()
            } else {
                premiumOff()
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch {
            balanceViewModel.balance.collect { balance ->
                val balanceTextView = findViewById<TextView>(R.id.balanceTextView)
                balanceTextView.text = balance
            }
        }
        lifecycleScope.launch {
            transactionViewModel.transactions.collect { transactionResponse ->
                transactionResponse?.let {
                    transactionAdapter.setData(it.data)
                }
            }
        }
    }

    private fun loadPremiumAdvice() {
        lifecycleScope.launch {
            adviceViewModel.adviceTitle.collect { title ->
                tvTitle.text = title
            }
        }
        lifecycleScope.launch {
            adviceViewModel.adviceDescription.collect { description ->
                tvDescription.text = description
            }
        }
    }
}
