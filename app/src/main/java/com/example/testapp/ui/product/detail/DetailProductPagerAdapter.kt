package com.example.testapp.ui.product.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.ui.product.detail.info.DescriptionFragment
import com.example.testapp.ui.product.detail.info.InfoFragment


class DetailProductPagerAdapter(
    fm: FragmentManager,
    private val product: Product,
    private val productVariants: List<ProductVariantWithImage>
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment {
        var frag: Fragment? = null
        when(position){
            0 -> frag = newInstance(product,productVariants)
            1 -> frag = newInstance(product.description)
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

    private fun newInstance(product: Product,productVariants: List<ProductVariantWithImage>) : InfoFragment {
        val args = Bundle()
        args.putParcelable("product",product)
        args.putParcelableArrayList("product_variants",productVariants as ArrayList<ProductVariantWithImage>)
        val frag = InfoFragment()
        frag.arguments=args
        return frag
    }
    private fun newInstance(description: String) : DescriptionFragment {
        val args = Bundle()
        args.putString("description",description)
        val frag = DescriptionFragment()
        frag.arguments=args
        return frag
    }
}