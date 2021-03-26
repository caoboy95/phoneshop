package com.example.testapp.ui.product.detail
import android.view.View
import androidx.navigation.findNavController
import com.example.testapp.Constant
import com.example.testapp.R
import com.example.testapp.data.db.entities.Product
import com.example.testapp.databinding.ProductViewAdapterBinding
import com.example.testapp.ui.formatCurrency
import com.example.testapp.ui.product.ProductFragmentDirections
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import java.text.NumberFormat
import java.util.*

class ProductItem(
    private val product: Product
) : BindableItem<ProductViewAdapterBinding>() {

    override fun getLayout() = R.layout.product_view_adapter
    private lateinit var clickListener: ClickListener
    val firebaseDatabase: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://dienthoaiviet-89c4a.appspot.com")


    override fun bind(viewBinding: ProductViewAdapterBinding, position: Int) {
        viewBinding.product = product
        val price = formatCurrency(product.unit_price) +
                if(product.promotion_price!=0) " (Sale ${product.promotion_price}%)" else ""
        viewBinding.textViewProductPrice.text = price
        viewBinding.textViewProductBrand.text = when(product.id_company){
            1 -> "by Apple"
            2 -> "by Samsung"
            3 -> "by Oppo"
            4 -> "by Xiaomi"
            5 -> "by Vivo"
            6 -> "by Realme"
            7 -> "by Oneplus"
            else-> null
        }
        var image = "product/${product.image}"
        firebaseDatabase.child(image).downloadUrl.addOnCompleteListener {
            it?.let {
                if (it.isSuccessful) {
                    Picasso.get().load(it.result).into(viewBinding.imageViewProductImage)
                }
            }
        }
        viewBinding.buttonPreview.setOnClickListener {
            val action = ProductFragmentDirections.startDetailFragment(product.id)
            it.findNavController().navigate(action)
        }

    }
    fun setOnProductClickListener(clickListener: ClickListener) {
        this.clickListener = clickListener
    }
    interface ClickListener {
        fun onItemClick(v: View, product: Product)
    }
}