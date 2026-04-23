package com.example.paybitodemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.button.MaterialButton
import com.paybito.sdk.models.CartItem
import com.paybito.sdk.models.PayBitoProduct

class ProductAdapter(
    private val products: List<PayBitoProduct>,
    private var cartItems: List<CartItem>,
    private val onAddToCart: (PayBitoProduct) -> Unit,
    private val onQtyChange: (String, Int) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivProductImage)
        val tvName: TextView = view.findViewById(R.id.tvProductName)
        val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val btnMinus: TextView = view.findViewById(R.id.btnMinus)
        val btnPlus: TextView = view.findViewById(R.id.btnPlus)
        val btnAdd: MaterialButton = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.tvName.text = product.name
        holder.tvPrice.text = "$${String.format("%.2f", product.price)}"
        
        val cartItem = cartItems.find { it.productId == product.productId }
        val qty = cartItem?.quantity ?: 0
        holder.tvQty.text = qty.toString()

        Glide.with(holder.ivImage.context)
            .load(product.imageUrl)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder.ivImage)

        holder.btnMinus.setOnClickListener {
            if (qty > 1) {
                onQtyChange(product.productId, qty - 1)
            } else if (qty == 1) {
                onQtyChange(product.productId, 0)
            }
        }

        holder.btnPlus.setOnClickListener {
            if (qty == 0) {
                onAddToCart(product.copy(quantity = 1))
            } else {
                onQtyChange(product.productId, qty + 1)
            }
        }

        holder.btnAdd.setOnClickListener {
            if (qty == 0) {
                onAddToCart(product.copy(quantity = 1))
            }
        }
        
        holder.btnAdd.text = if (qty > 0) "In Cart" else "Add"
        holder.btnAdd.alpha = if (qty > 0) 0.6f else 1.0f
    }

    override fun getItemCount() = products.size

    fun updateCart(newCartItems: List<CartItem>) {
        this.cartItems = newCartItems
        notifyDataSetChanged()
    }
}
