package com.example.testapp.ui.product.view.detail.info

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.Constant.PRODUCT_KEY
import com.example.testapp.Constant.SELECTED_VARIANT_KEY
import com.example.testapp.R
import com.example.testapp.data.db.entities.*
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.ProductInfoRepository
import com.example.testapp.databinding.InfoFragmentBinding
import com.example.testapp.databinding.ProductVariantItemAdapterBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.product.adapter.ProductInfoAdapter
import com.example.testapp.ui.product.viewmodel.InfoViewModel
import com.example.testapp.ui.visible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch


class InfoFragment() : BaseFragment<InfoViewModel, InfoFragmentBinding, ProductInfoRepository>() {

    private var _selectedProductVariant: ProductVariant? = null
    val selectedProductVariant : ProductVariant?
        get() = _selectedProductVariant

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product: Product = arguments?.getParcelable<Product>(PRODUCT_KEY) as Product
        val productVariants: ArrayList<ProductVariant> = arguments?.getParcelableArrayList<ProductVariant>("product_variants") as ArrayList<ProductVariant>
        binding.progressBar.visible(true)
        updateUI(product, productVariants)
        binding.progressBar.visible(false)
    }

    private fun updateUI(product: Product, productVariants: List<ProductVariant>) {
        binding.textViewName.text = (product.name + if (product.promotion_price!=0) " (Sale ${product.promotion_price}%)" else "" )
//        lifecycleScope.launch {
//            binding.textViewBrand.text = viewModel.getBrand(product.id_company).await().let {
//                when(it) {
//                    is Resource.Success -> it.value.name
//                    else -> resources.getString(R.string.unknow)
//                }
//            }
//            binding.textViewType.text = viewModel.getType(product.id_type).await().let {
//                when(it) {
//                    is Resource.Success -> it.value.name
//                    else -> resources.getString(R.string.unknow)
//                }
//            }
//        }
        viewModel.getTypeFromFirebase(product.id_type).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        it?.let { dataSnapshot ->
                            binding.textViewType.text = dataSnapshot.getValue(TypeProduct::class.java)?.name
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.textViewType.text = resources.getString(R.string.unknow)
            }
        })
        viewModel.getBrandFromFirebase(product.id_company).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        it?.let { dataSnapshot ->
                            binding.textViewBrand.text = dataSnapshot.getValue(Brand::class.java)?.name
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                binding.textViewBrand.text = resources.getString(R.string.unknow)
            }

        })
        initRecyclerView(productVariants, product.promotion_price)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): InfoFragmentBinding = InfoFragmentBinding.inflate(inflater, container, false)

    override fun getViewModel() = InfoViewModel::class.java

    override fun getFragmentRepository(
            networkConnectionInterceptor: NetworkConnectionInterceptor
    ) = ProductInfoRepository(remoteDataSource.buildApi(ProductApi::class.java, networkConnectionInterceptor))

    private fun initRecyclerView(productVariants: List<ProductVariant>, promotionPrice: Int) {
        val mAdapter = ProductInfoAdapter()
        mAdapter.setData(productVariants, promotionPrice)
        mAdapter.setOnVariantClickListener(object : ProductInfoAdapter.ClickListener {
            override fun onItemVariantClick(binding: ProductVariantItemAdapterBinding, productVariant: ProductVariant) {
                _selectedProductVariant = productVariant
                val bundle = Bundle()
                bundle.putParcelable(SELECTED_VARIANT_KEY, _selectedProductVariant)
                parentFragment?.arguments = bundle
            }
        })
        binding.recyclerViewInfo.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    companion object {
        private const val TAG = "InfoFragment"
    }
}