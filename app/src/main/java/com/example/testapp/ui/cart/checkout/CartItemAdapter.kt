package com.example.testapp.ui.cart.checkout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.Constant
import com.example.testapp.data.db.entities.CartItem
import com.example.testapp.databinding.CartItemAdapterBinding
import com.example.testapp.ui.cart.CartViewAdapter
import com.example.testapp.ui.visible
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class CartItemAdapter: RecyclerView.Adapter<CartItemAdapter.CartItemHolder>() {

    private var cartItems = emptyList<CartItem>()
    fun setData(cartItem: List<CartItem>) {
        this.cartItems = cartItem
        notifyDataSetChanged()
    }

    class CartItemHolder(val binding: CartItemAdapterBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(cartItem: CartItem){
            binding.cartItem=cartItem
            binding.textViewProductPrice.text= NumberFormat.getCurrencyInstance(Locale("vn","VN")).format(cartItem.price)
            val uri = Constant.URL_IMAGE+"product/"+cartItem.item.image.link
            Picasso.get().load(uri).into(binding.imageViewProduct)
            binding.buttonRemoveCartItem.visible(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding = CartItemAdapterBinding.inflate(inflater,parent,false)
        return CartItemHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int =cartItems.size
}