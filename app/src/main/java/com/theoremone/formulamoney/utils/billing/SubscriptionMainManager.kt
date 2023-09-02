package com.theoremone.formulamoney.utils.billing

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*

@Suppress("DEPRECATION")
@SuppressLint("StaticFieldLeak")
class SubscriptionMainManager(
    application: Application,
    private val activity: Activity
) : AndroidViewModel(application), PurchasesUpdatedListener {
    private val billingClient: BillingClient = BillingClient.newBuilder(activity)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    val isTheoremOneSubscriptionTestStarted: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSubscribed: MutableLiveData<Boolean> = MutableLiveData(false)

    init {
        setupBillingClient()
    }

    fun setupBillingClient() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK || isTheoremOneTesting) {
                    checkSubscriptionStatus()
                }
            }

            override fun onBillingServiceDisconnected() {
                Handler(Looper.getMainLooper()).postDelayed({
                    setupBillingClient()
                }, 10000)
            }
        })
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null || isTheoremOneTesting) {
            checkSubscriptionStatus()
        } else {
            isSubscribed.value = false
        }
    }

    fun checkSubscriptionStatus() {
        val params = SkuDetailsParams.newBuilder()
            .setSkusList(listOf(PREMIUM_SUBSCRIPTION_SKU))
            .setType(BillingClient.SkuType.SUBS)
            .build()
        billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                for (skuDetails in skuDetailsList) {
                    if (skuDetails.sku == PREMIUM_SUBSCRIPTION_SKU) {
                        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.SkuType.SUBS)
                            .build()
                        billingClient.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchasesList ->
                            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                                for (purchase in purchasesList) {
                                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                        if (!purchase.isAcknowledged) {
                                            isSubscribed.value = true
                                        }
                                        isSubscribed.postValue(true)
                                        return@queryPurchasesAsync
                                    }
                                }
                                isSubscribed.postValue(false)
                            }
                        }
                        return@querySkuDetailsAsync
                    }
                }
                theoremOneTestBackdoor()
            } else {
                theoremOneTestBackdoor()
            }
        }
    }

    fun startSubscriptionFlow() {
        if (isTheoremOneTesting) isTheoremOneSubscriptionTestStarted.value = true
        val params = BillingFlowParams.newBuilder()
            .setSkuDetails(SkuDetails(skuDetailsJson))
            .build()
        billingClient.launchBillingFlow(activity, params)
    }

    private fun theoremOneTestBackdoor() {
        if (isTheoremOneSubscriptionTestStarted.value == true) {
            isSubscribed.postValue(true)
        }
    }

    companion object {
        // This constant defines if we're setting up a fake subscription test
        const val isTheoremOneTesting = true

        // SKU that should be previously registered in Play Console
        const val PREMIUM_SUBSCRIPTION_SKU = "premium_subscription_1"
    }

    private val skuDetailsJson = """
        {
            "productId":"premium_subscription_1",
            "type":"subs",
            "price":"US$ 99.80",
            "price_amount_micros":99800000,
            "price_currency_code":"USD",
            "title":"Premium",
            "description":"Get insights on how to save money using premium advice"
        }
        """
}
