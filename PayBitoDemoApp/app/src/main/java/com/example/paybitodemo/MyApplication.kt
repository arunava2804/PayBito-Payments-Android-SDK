package com.example.paybitodemo

import android.app.Application
import com.paybito.sdk.PayBitoConfig
import com.paybito.sdk.PayBitoSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. Initialize the SDK with merchant credentials
        val config = PayBitoConfig(
            merchantId = 26660L,
            publicKey  = "pk_5A2CC3DD9B886DC398EFBE756634277B2F1BF34998E719C8C96A4D0D6551571E",
            brokerId   = "ARNA02042026142506",
            origin     = "https://coulombworld.com/",
            enableDebugLogs = true
        )

        PayBitoSdk.init(this, config)
    }
}
