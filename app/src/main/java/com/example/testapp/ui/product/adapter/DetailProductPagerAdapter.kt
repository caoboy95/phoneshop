package com.example.testapp.ui.product.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.ui.product.view.detail.info.DescriptionFragment
import com.example.testapp.ui.product.view.detail.info.InfoFragment


class DetailProductPagerAdapter(
    fm: FragmentManager,
    val product: Product
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var productVariants = arrayListOf<ProductVariant>()

    fun setData(productVariants: List<ProductVariant>) {
        this.productVariants = productVariants as ArrayList<ProductVariant>
        Log.e("adapterdetail", this.productVariants.toString())
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        var frag: Fragment? = null
        Log.e("adapterdetail", "add item")
        when(position){
            0 -> frag = newInfoInstance(product, productVariants)
            1 -> frag = newDescriptionInstance(product.description)
        }
        return frag as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String=""
        when(position){
            0 -> title = "Thông Tin"
            1 -> title = "Mô Tả"
        }
        return title
    }

    private fun newInfoInstance(product: Product, productVariants: ArrayList<ProductVariant>) : InfoFragment {
        val args = Bundle()
        args.putParcelable("product", product)
        args.putParcelableArrayList("product_variants", productVariants)
        val frag = InfoFragment()
        frag.arguments = args
        return frag
    }

    private fun newDescriptionInstance(description: String) : DescriptionFragment {
        val args = Bundle()
        args.putString("description", description)
        val frag = DescriptionFragment()
        frag.arguments = args
        return frag
    }
}