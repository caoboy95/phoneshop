package com.example.testapp.ui.product.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.databinding.ProductVariantItemAdapterBinding
import com.example.testapp.ui.formatCurrency

class ProductInfoAdapter : RecyclerView.Adapter<ProductInfoAdapter.ProductInfoHolder>() {

    var lastCheckedPosition: Int = 0
    var productVariants = emptyList<ProductVariant>()
    var promotionPrice = 0
    private lateinit var clickListener: ClickListener

    fun setData(productVariants: List<ProductVariant>, promotionPrice: Int) {
        this.productVariants = productVariants
        this.promotionPrice = promotionPrice
        notifyDataSetChanged()
    }

    class ProductInfoHolder(
            val binding: ProductVariantItemAdapterBinding,
            val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(productVariant: ProductVariant, clickListener: ClickListener, promotionPrice: Int) {
            val version = "${productVariant.version} - ${productVariant.color}"
            if(productVariant.quantity == 0) {
                binding.rootLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.out_of_stock))
                binding.rootLayout.isEnabled= false
            }
            val price = formatCurrency((productVariant.unit_price *(100 - promotionPrice) / 100))
            binding.textViewVersion.text = version
            binding.textViewPrice.text = price
            binding.radioButtonSelectVariant.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked) clickListener.onItemVariantClick(binding, productVariant)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductInfoHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductVariantItemAdapterBinding.inflate(inflater, parent, false)
        return ProductInfoHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: ProductInfoHolder, position: Int) {
        holder.bind(productVariants[position], clickListener, promotionPrice)
        holder.binding.radioButtonSelectVariant.isChecked = (lastCheckedPosition == position)
        holder.binding.rootLayout.setOnClickListener {
            if (position == lastCheckedPosition) {
                holder.binding.radioButtonSelectVariant.isChecked = true
                lastCheckedPosition = -1
            } else {
                lastCheckedPosition = position
                notifyDataSetChanged()
            }
        }
        holder.binding.radioButtonSelectVariant.isClickable = false
    }

    override fun getItemCount(): Int = productVariants.size

    fun setOnVariantClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }

    interface ClickListener {
        fun onItemVariantClick(binding: ProductVariantItemAdapterBinding, productVariant: ProductVariant)
    }
}