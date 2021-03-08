package com.example.testapp.ui.cart

import android.text.Layout
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.Constant
import com.example.testapp.data.db.entities.CartItem
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.databinding.CartItemAdapterBinding
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class CartViewAdapter: RecyclerView.Adapter<CartViewAdapter.CartHolder>() {

    private lateinit var clickListener:ClickListener
    var cartItems = emptyList<CartItem>()
    fun setData(cartItem: List<CartItem>) {
        this.cartItems = cartItem
        notifyDataSetChanged()
    }

    class CartHolder(val binding: CartItemAdapterBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(cartItem: CartItem,clickListener: ClickListener){
            binding.cartItem=cartItem
            binding.textViewProductPrice.text= NumberFormat.getCurrencyInstance(Locale("vn","VN")).format(cartItem.price)
            val uri = Constant.URL_IMAGE+"product/"+cartItem.item.image.link
            Picasso.get().load(uri).into(binding.imageViewProduct)
            binding.buttonRemoveCartItem.setOnClickListener {
                clickListener.onRemoveClickListener(cartItem.item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        val inflater= LayoutInflater.from(parent.context)
        val binding = CartItemAdapterBinding.inflate(inflater,parent,false)
        return CartHolder(binding)
    }

    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        holder.bind(cartItems[position],clickListener)

    }

    override fun getItemCount(): Int = cartItems.size

    fun setOnRemoveClickListener(clickListener: ClickListener){
        this.clickListener=clickListener
    }

    interface ClickListener{
        fun onRemoveClickListener(item: ProductVariantWithImage)
    }
}