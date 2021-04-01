package com.example.testapp.ui.product.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.testapp.Constant.DESCRIPTION_KEY
import com.example.testapp.Constant.PRODUCT_KEY
import com.example.testapp.R
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.ui.product.view.detail.info.DescriptionFragment
import com.example.testapp.ui.product.view.detail.info.InfoFragment


class DetailProductPagerAdapter(
    fm: FragmentManager,
    val context: Context,
    val product: Product
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var productVariants = arrayListOf<ProductVariant>()

    fun setData(productVariants: List<ProductVariant>) {
        this.productVariants = productVariants as ArrayList<ProductVariant>
        notifyDataSetChanged()
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        var frag: Fragment? = null
        when(position){
            0 -> frag = newInfoInstance(product, productVariants)
            1 -> frag = newDescriptionInstance(product.description)
        }
        return frag as Fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title: String = ""
        when(position){
            0 -> title = context.resources.getString(R.string.title_information)
            1 -> title = context.resources.getString(R.string.title_description)
        }
        return title
    }

    private fun newInfoInstance(product: Product, productVariants: ArrayList<ProductVariant>) : InfoFragment {
        val args = Bundle()
        args.putParcelable(PRODUCT_KEY, product)
        args.putParcelableArrayList("product_variants", productVariants)
        val frag = InfoFragment()
        frag.arguments = args
        return frag
    }

    private fun newDescriptionInstance(description: String) : DescriptionFragment {
        val args = Bundle()
        args.putString(DESCRIPTION_KEY, description)
        val frag = DescriptionFragment()
        frag.arguments = args
        return frag
    }
}