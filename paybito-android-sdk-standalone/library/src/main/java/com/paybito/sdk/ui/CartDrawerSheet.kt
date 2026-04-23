package com.paybito.sdk.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.lifecycle.lifecycleScope
import com.paybito.sdk.PayBitoSdk
import com.paybito.sdk.R
import com.paybito.sdk.models.CartItem
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartDrawerSheet : BottomSheetDialogFragment() {

    private lateinit var tvTotal: TextView
    private lateinit var rvItems: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        return inflater.inflate(R.layout.pb_sheet_cart_drawer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set background to transparent to allow rounded corners from layout
        (view.parent as? View)?.setBackgroundColor(android.graphics.Color.TRANSPARENT)

        val brokerName = PayBitoSdk.getBrokerName()
        view.findViewById<TextView>(R.id.tvCartTitle)?.text = "Your $brokerName Cart"
        view.findViewById<TextView>(R.id.tvSecuredBy)?.text = "🔒 Secured by $brokerName"

        rvItems = view.findViewById(R.id.rvCartItems)
        tvTotal = view.findViewById(R.id.tvDrawerTotal)
        val btnCheckout = view.findViewById<Button>(R.id.btnCheckout)

        rvItems.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            PayBitoSdk.cartState.collectLatest { state ->
                tvTotal.text = "$${String.format("%.2f", state.total)}"
                rvItems.adapter = CartItemAdapter(state.items) { productId, newQty ->
                    lifecycleScope.launch {
                        PayBitoSdk.updateQuantity(productId, newQty)
                    }
                }

                // Handle empty state inside drawer
                if (state.count == 0) {
                    btnCheckout.isEnabled = false
                    btnCheckout.alpha = 0.5f
                    dismiss()
                } else {
                    btnCheckout.isEnabled = true
                    btnCheckout.alpha = 1.0f
                }
            }
        }

        btnCheckout.setOnClickListener {
            dismiss()
            PayBitoSdk.checkout(requireActivity())
        }
    }

    companion object {
        fun newInstance() = CartDrawerSheet()
    }
}

class CartItemAdapter(
    private val items: List<CartItem>,
    private val onQtyChange: (String, Int) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImage: ImageView = view.findViewById(R.id.ivItemImage)
        val tvName: TextView = view.findViewById(R.id.tvItemName)
        val tvQty: TextView = view.findViewById(R.id.tvItemQty)
        val tvUnitPrice: TextView = view.findViewById(R.id.tvItemUnitPrice)
        val tvSubtotal: TextView = view.findViewById(R.id.tvItemSubtotal)
        val btnMinus: TextView = view.findViewById(R.id.btnItemMinus)
        val btnPlus: TextView = view.findViewById(R.id.btnItemPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pb_item_cart_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvQty.text = item.quantity.toString()
        holder.tvUnitPrice.text = "x $${item.unitPrice}"
        holder.tvSubtotal.text = "$${String.format("%.2f", item.unitPrice * item.quantity)}"
        
        Glide.with(holder.ivImage.context)
            .load(item.imageUrl)
            .centerCrop()
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(holder.ivImage)

        holder.btnMinus.setOnClickListener {
            onQtyChange(item.productId, item.quantity - 1)
        }

        holder.btnPlus.setOnClickListener {
            onQtyChange(item.productId, item.quantity + 1)
        }
    }

    override fun getItemCount() = items.size
}
