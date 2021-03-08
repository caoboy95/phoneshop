package com.example.testapp.ui.product.detail.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.ProductInfoRepository
import com.example.testapp.databinding.InfoFragmentBinding
import com.example.testapp.databinding.ProductVariantItemAdapterBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.visible
import kotlinx.coroutines.launch


class InfoFragment() : BaseFragment<InfoViewModel,InfoFragmentBinding,ProductInfoRepository>() {

    private var _selectedProductVariant: ProductVariantWithImage? = null
    val selectedProductVariant : ProductVariantWithImage?
        get() = _selectedProductVariant

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product: Product = arguments?.getParcelable<Product>("product") as Product
        val productVariants: List<ProductVariantWithImage> = arguments?.getParcelableArrayList<ProductVariantWithImage>("product_variants") as List<ProductVariantWithImage>
        binding.progressBar.visible(true)
        updateUI(product,productVariants)
        binding.progressBar.visible(false)

    }
    fun updateUI(product: Product,productVariants: List<ProductVariantWithImage>){
        binding.textViewName.text = (product.name+ if(product.promotion_price!=0) " (Sale "+product.promotion_price+"%)" else "" )
        lifecycleScope.launch {
            binding.textViewBrand.text = viewModel.getBrand(product.id_company).await().let {
                when(it){
                    is Resource.Success -> it.value.name
                    else -> "Không"
                }
            }
            binding.textViewType.text = viewModel.getType(product.id_type).await().let {
                when(it){
                    is Resource.Success -> it.value.name
                    else -> "Không"
                }
            }
        }
        initRecyclerView(productVariants,product.promotion_price)

    }
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): InfoFragmentBinding = InfoFragmentBinding.inflate(inflater,container,false)

    override fun getViewModel() = InfoViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor)
    = ProductInfoRepository(remoteDataSource.buildApi(ProductApi::class.java,networkConnectionInterceptor))

    private fun initRecyclerView(productVariants: List<ProductVariantWithImage>,promotionPrice: Int) {
        val mAdapter = ProductInfoAdapter()
        mAdapter.setData(productVariants,promotionPrice)
        mAdapter.setOnVariantClickListener(object : ProductInfoAdapter.ClickListener {
            override fun onItemVariantClick(binding: ProductVariantItemAdapterBinding, productVariant: ProductVariantWithImage) {
                _selectedProductVariant= productVariant
                val bundle = Bundle()
                bundle.putParcelable("selected",_selectedProductVariant)
                parentFragment?.arguments=bundle
            }
        })
        binding.recyclerViewInfo.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }
}