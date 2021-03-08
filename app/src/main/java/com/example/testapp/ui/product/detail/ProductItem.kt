package com.example.testapp.ui.product.detail
import android.view.View
import androidx.navigation.findNavController
import com.example.testapp.Constant
import com.example.testapp.R
import com.example.testapp.data.db.entities.Product
import com.example.testapp.databinding.ProductViewAdapterBinding
import com.example.testapp.ui.product.ProductFragmentDirections
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import java.text.NumberFormat
import java.util.*

class ProductItem(
    private val product: Product
) : BindableItem<ProductViewAdapterBinding>(){

    override fun getLayout() = R.layout.product_view_adapter
    private lateinit var clickListener: ClickListener

    override fun bind(viewBinding: ProductViewAdapterBinding, position: Int) {
        viewBinding.product=product
        val price = NumberFormat.getCurrencyInstance(Locale("vn","VN")).format(product.unit_price)+
                if(product.promotion_price!=0) " (Sale "+product.promotion_price+"%)" else ""
        viewBinding.textViewProductPrice.text= price
        viewBinding.textViewProductBrand.text= when(product.id_company){
            1 -> "by Apple"
            2 -> "by Samsung"
            3 -> "by Oppo"
            4 -> "by Xiaomi"
            5 -> "by Vivo"
            6 -> "by Realme"
            7 -> "by Oneplus"
            else-> null
        }
        var image = Constant.URL_IMAGE+"product/"+product.image
        Picasso.get().load(image).into(viewBinding.imageViewProductImage)
        viewBinding.buttonPreview.setOnClickListener {
            val action = ProductFragmentDirections.startDetailFragment(product.id)
            it.findNavController().navigate(action)
        }

    }
    fun setOnProductClickListener(clickListener: ClickListener){
        this.clickListener= clickListener
    }
    interface ClickListener{
        fun onItemClick(v: View, product: Product)
    }
}