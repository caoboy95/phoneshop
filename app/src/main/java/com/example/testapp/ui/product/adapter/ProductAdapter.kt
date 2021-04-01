package com.example.testapp.ui.product.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R
import com.example.testapp.data.db.entities.Brand
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.repository.ProductRepository
import com.example.testapp.databinding.ProductViewAdapterBinding
import com.example.testapp.ui.formatCurrency
import com.example.testapp.ui.product.view.ProductFragmentDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.supervisorScope

class ProductAdapter(
    private val productRepository: ProductRepository
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var products = listOf<Product>()
    private var brands = listOf<Brand>()

    fun setData(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductViewAdapterBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position], productRepository)
    }

    override fun getItemCount(): Int = products.size

    class ProductViewHolder(
        val binding: ProductViewAdapterBinding,
        val context: Context
        ): RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product, productRepository: ProductRepository) {
            binding.product = product
            val price = formatCurrency(product.unit_price) +
                    if(product.promotion_price != 0) " (Sale ${product.promotion_price}%)" else ""
            binding.textViewProductPrice.text = price
            getBrands(product.id_company, productRepository)
            productRepository.getProductImageFromFirebase(product.image).addOnSuccessListener {
                Picasso.get().load(it).placeholder(R.drawable.placeholder).into(binding.imageViewProductImage)
            }.addOnFailureListener {
                Log.e(TAG, "Error: $it")
            }
            binding.buttonPreview.setOnClickListener {
                val action = ProductFragmentDirections.startDetailFragment(product)
                it.findNavController().navigate(action)
            }
        }

        private fun getBrands(brandID: Int, productRepository: ProductRepository) {
            var brandName = "by Unknown"
            productRepository.getBrandFromFirebase(brandID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach {
                                it.getValue(Brand::class.java)?.let { brand ->
                                    brandName = "by ${brand.name}"
                                }
                            }
                        }
                        binding.textViewProductBrand.text = brandName
                    }

                    override fun onCancelled(error: DatabaseError) {
                        binding.textViewProductBrand.text = context.resources.getString(R.string.unknow)
                    }
                })
        }
    }

    companion object {
        private const val TAG = "ProductAdapter"
    }
}