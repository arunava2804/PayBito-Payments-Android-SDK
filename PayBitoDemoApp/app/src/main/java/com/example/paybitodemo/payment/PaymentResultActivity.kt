package com.example.paybitodemo.payment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.paybitodemo.databinding.ActivityPaymentResultBinding
import com.paybito.sdk.PayBitoSdk
import kotlinx.coroutines.launch

class PaymentResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = intent?.data
        if (uri == null) {
            finish()
            return
        }

        // uri: paybito://checkout/success?paymentId=...
        // uri: paybito://checkout/failure
        
        when {
            uri.host == "checkout" && uri.path == "/success" -> {
                showSuccess()
                // Clear cart on success
                lifecycleScope.launch {
                    PayBitoSdk.clearCart()
                }
            }
            uri.host == "checkout" && uri.path == "/failure" -> {
                showFailure()
            }
            else -> finish()
        }

        binding.btnBackHome.setOnClickListener {
            // Ensure cart is cleared before returning, even if onCreate clear was fast
            lifecycleScope.launch {
                PayBitoSdk.clearCart()
                finish()
            }
        }

        // Set dynamic broker name
        binding.tvSecuredBy.text = "Secured by ${PayBitoSdk.getBrokerName()}"
    }

    private fun showSuccess() {
        binding.tvStatusTitle.text = "Order Placed\nSuccessfully!"
        binding.tvStatusMessage.text = "Your payment has been processed and your order is confirmed. A confirmation will be sent shortly."
        binding.ivStatusIcon.setImageResource(android.R.drawable.checkbox_on_background)
        binding.ivStatusIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#22C97A"))
    }

    private fun showFailure() {
        binding.tvStatusTitle.text = "Payment Failed"
        binding.tvStatusMessage.text = "Something went wrong with your payment. Please try again or contact support."
        binding.ivStatusIcon.setImageResource(android.R.drawable.ic_delete)
        binding.ivStatusIcon.imageTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EF4444"))
    }
}
