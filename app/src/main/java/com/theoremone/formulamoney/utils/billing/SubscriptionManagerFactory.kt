package com.theoremone.formulamoney.utils.billing

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SubscriptionManagerFactory(
    private val application: Application,
    private val activity: Activity
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SubscriptionMainManager(application, activity) as T
    }
}
