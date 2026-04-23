package com.example.paybitodemo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.paybitodemo.databinding.ActivityMainBinding
import com.paybito.sdk.PayBitoSdk
import com.paybito.sdk.models.PayBitoProduct
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        // 2. Clear broken legacy items from previous runs once
        val prefs = getSharedPreferences("demo_prefs", MODE_PRIVATE)
        if (!prefs.getBoolean("cart_fixed_v5", false)) {
            lifecycleScope.launch {
                PayBitoSdk.clearCart()
                prefs.edit().putBoolean("cart_fixed_v5", true).apply()
            }
        }

        // 3. Show the Cart Drawer
        binding.rlCartTrigger.setOnClickListener {
            PayBitoSdk.openCart(this)
        }

        binding.btnProceedToCheckout.setOnClickListener {
            PayBitoSdk.checkout(this)
        }

        // 4. Observe cart state to keep everything in sync
        lifecycleScope.launch {
            PayBitoSdk.cartState.collect { state ->
                updateCartStatus(state.count)
                adapter.updateCart(state.items)
            }
        }
    }

    private fun setupRecyclerView() {
        val products = listOf(
            PayBitoProduct(
                productId = "P001",
                name      = "Studio Headphones",
                price     = 89.99,
                imageUrl  = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800"
            ),
            PayBitoProduct(
                productId = "P002",
                name      = "Smart Watch v2",
                price     = 299.00,
                imageUrl  = "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800"
            ),
            PayBitoProduct(
                productId = "P003",
                name      = "Wireless Speaker",
                price     = 45.50,
                imageUrl  = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800"
            ),
            PayBitoProduct(
                productId = "P004",
                name      = "Pro Gaming Mouse",
                price     = 59.99,
                imageUrl  = "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800"
            ),
            PayBitoProduct(
                productId = "P005",
                name      = "Mechanical Keyboard",
                price     = 120.00,
                imageUrl  = "https://images.unsplash.com/photo-1511467687858-23d96c32e4ae?w=800"
            ),
            PayBitoProduct(
                productId = "P006",
                name      = "Ultra HD Camera",
                price     = 75.00,
                imageUrl  = "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800"
            ),
            PayBitoProduct(
                productId = "P007",
                name      = "Premium Earbuds",
                price     = 129.99,
                imageUrl  = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=800"
            ),
            PayBitoProduct(
                productId = "P008",
                name      = "Office Laptop",
                price     = 899.00,
                imageUrl  = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800"
            ),
            PayBitoProduct(
                productId = "P009",
                name      = "4K Display",
                price     = 450.00,
                imageUrl  = "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=800"
            ),
            PayBitoProduct(
                productId = "P010",
                name      = "Desk Accessory",
                price     = 35.00,
                imageUrl  = "https://images.unsplash.com/photo-1534073828943-f801091bb18c?w=800"
            )
        )

        adapter = ProductAdapter(
            products = products,
            cartItems = PayBitoSdk.cartState.value.items,
            onAddToCart = { product ->
                lifecycleScope.launch {
                    try {
                        PayBitoSdk.addToCart(product)
                        Toast.makeText(this@MainActivity, "Added to cart", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            onQtyChange = { productId, newQty ->
                lifecycleScope.launch {
                    PayBitoSdk.updateQuantity(productId, newQty)
                }
            }
        )

        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = adapter
    }

    private fun updateCartStatus(count: Int) {
        binding.tvCartCount.text = "Items in Cart: $count"
        binding.tvCartTotal.text = "Total Amount: $${String.format("%.2f", PayBitoSdk.getTotal())}"
        
        if (count > 0) {
            binding.tvCartBadge.visibility = View.VISIBLE
            binding.tvCartBadge.text = count.toString()
            binding.btnProceedToCheckout.isEnabled = true
            binding.btnProceedToCheckout.alpha = 1.0f
            binding.rlCartTrigger.alpha = 1.0f
        } else {
            binding.tvCartBadge.visibility = View.GONE
            binding.btnProceedToCheckout.isEnabled = false
            binding.btnProceedToCheckout.alpha = 0.5f
            binding.rlCartTrigger.alpha = 0.5f
        }
    }
}
