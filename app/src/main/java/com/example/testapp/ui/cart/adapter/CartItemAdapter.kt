package com.example.testapp.ui.cart.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.data.db.entities.CartItem
import com.example.testapp.data.db.entities.Image
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.repository.CartRepository
import com.example.testapp.databinding.CartItemAdapterBinding
import com.example.testapp.ui.formatCurrency
import com.example.testapp.ui.getDataValue
import com.example.testapp.ui.visible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

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
            loadImageToView(repository, cartItem.item.id_image)
            loadProductNameToTextView(repository, cartItem.item.id_product)
        }

        private fun loadImageToView(repository: CartRepository, imageID: Int) {
            repository.getImage(imageID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val images = snapshot.getDataValue(Image::class.java)

                    if (images.isNotEmpty()) {
                        repository.getProductImageFromFirebase(images.first().link).addOnSuccessListener { uri ->
                            Picasso.get().load(uri).into(binding.imageViewProduct)
                        }
                        return
                    }
                    Log.e(TAG, "image is empty")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG,"Error: $error")
                }

            })
        }

        private fun loadProductNameToTextView(repository: CartRepository, productID: Int) {
            repository.getProduct(productID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val products = snapshot.getDataValue(Product::class.java)

                    if (products.isNotEmpty()) {
                        binding.textViewProductName.text = products.first().name
                        return
                    }
                    Log.e(TAG, "Product is empty")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error: $error")
                }

            })
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

    companion object {
        const val TAG = "CartItemAdapter"
    }
}