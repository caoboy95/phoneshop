package com.example.testapp.ui.cart.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.Constant
import com.example.testapp.R
import com.example.testapp.data.db.entities.CartItem
import com.example.testapp.data.db.entities.Image
import com.example.testapp.data.repository.CartRepository
import com.example.testapp.databinding.CartItemAdapterBinding
import com.example.testapp.ui.cart.CartViewAdapter
import com.example.testapp.ui.formatCurrency
import com.example.testapp.ui.visible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class CartItemAdapter(val repository: CartRepository): RecyclerView.Adapter<CartItemAdapter.CartItemHolder>() {

    private var cartItems = emptyList<CartItem>()
    fun setData(cartItem: List<CartItem>) {
        this.cartItems = cartItem
        notifyDataSetChanged()
    }

    class CartItemHolder(val binding: CartItemAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem, repository: CartRepository) {
            binding.cartItem = cartItem
            binding.textViewProductPrice.text = formatCurrency(cartItem.price)
            binding.buttonRemoveCartItem.visible(false)
            repository.loadImageToView(cartItem.item.id_image, binding.imageViewProduct)
            repository.loadProductNameToTextView(cartItem.item.id_product, binding.textViewProductName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartItemAdapterBinding.inflate(inflater, parent, false)
        return CartItemHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemHolder, position: Int) {
        holder.bind(cartItems[position], repository)
    }

    override fun getItemCount(): Int = cartItems.size
}