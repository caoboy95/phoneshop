package com.example.testapp.ui.cart

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.Constant
import com.example.testapp.R
import com.example.testapp.data.db.entities.*
import com.example.testapp.data.repository.CartRepository
import com.example.testapp.databinding.CartItemAdapterBinding
import com.example.testapp.ui.formatCurrency
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class CartViewAdapter(
        val repository: CartRepository
): RecyclerView.Adapter<CartViewAdapter.CartHolder>() {

    private lateinit var clickListener: ClickListener
    var cartItems = emptyList<CartItem>()

    fun setData(cartItem: List<CartItem>) {
        this.cartItems = cartItem
        notifyDataSetChanged()
    }

    class CartHolder(
            val binding: CartItemAdapterBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem, clickListener: ClickListener, repository: CartRepository) {
            binding.cartItem = cartItem
            binding.textViewProductPrice.text = formatCurrency(cartItem.price)
            repository.loadProductNameToTextView(cartItem.item.id_product, binding.textViewProductName)
            repository.loadImageToView(cartItem.item.id_image, binding.imageViewProduct)
            binding.buttonRemoveCartItem.setOnClickListener {
                clickListener.onRemoveClickListener(cartItem.item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding = CartItemAdapterBinding.inflate(inflater, parent, false)
        return CartHolder(binding)
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        holder.bind(cartItems[position], clickListener, repository)
    }

    override fun getItemCount(): Int = cartItems.size

    fun setOnRemoveClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onRemoveClickListener(item: ProductVariant)
    }
}