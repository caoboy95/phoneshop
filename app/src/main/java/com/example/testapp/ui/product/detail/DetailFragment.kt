package com.example.testapp.ui.product.detail

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testapp.Constant
import com.example.testapp.R
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.ProductDetailRepository
import com.example.testapp.data.response.ProductDetailResponse
import com.example.testapp.databinding.FragmentDetailBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.handleApiError
import com.example.testapp.ui.snackbar
import com.example.testapp.ui.visible
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import java.util.*


class DetailFragment : BaseFragment<ProductDetailViewModel, FragmentDetailBinding, ProductDetailRepository>() {

    val safeArgs: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        if(safeArgs.id!=0){
            viewModel.setID(safeArgs.id)
        }
        binding.progressBar.visible(true)
        viewModel.getProductDetailFromApi(viewModel.productID)
        viewModel.product.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    viewModel.getProductVariantsFromApi(viewModel.productID)
                    viewModel.productVariants.observe(viewLifecycleOwner, Observer { productVariants ->
                        when (productVariants) {
                            is Resource.Success -> {
                                binding.progressBar.visible(false)
                                updateUI(it.value)
                                addViewPagerControl(it.value.product, productVariants.value.product_variants)
                            }
                            is Resource.Loading -> {
                                binding.progressBar.visible(true)
                            }
                            is Resource.Failure -> {
                                binding.progressBar.visible(false)
                                this@DetailFragment.handleApiError(productVariants)
                            }
                        }
                    })
                    binding.progressBar.visible(false)
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                    this@DetailFragment.handleApiError(it)
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cart_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.from_right)
                .setPopExitAnim(R.anim.exit_right)
                .setPopEnterAnim(R.anim.from_left)
                .setExitAnim(R.anim.exit_left)
                .setLaunchSingleTop(true)
                .build()
        when(item.itemId){
            R.id.menu_item_cart -> this.view?.findNavController()?.navigate(R.id.cartFragment,null,navOptions)
        }
        return super.onOptionsItemSelected(item)
    }

    fun addViewPagerControl(product : Product, productVariants: List<ProductVariantWithImage>) {
        val manager :FragmentManager = childFragmentManager
        val pagerAdapter = DetailProductPagerAdapter(manager, product, productVariants)
        binding.pager.adapter = pagerAdapter
        binding.pager.offscreenPageLimit = 1
        binding.tabLayout.setupWithViewPager(binding.pager)
    }

    fun updateUI(productResponse: ProductDetailResponse) {
        val uri = "${Constant.URL_IMAGE}product/${productResponse.product.image}"
        Log.e(TAG, uri)
        Picasso.get().load(uri).into(binding.imageViewProductImage)
        binding.buttonAddToCart.setOnClickListener {
            val productVariant = this.arguments?.getParcelable<ProductVariantWithImage>("selected") as ProductVariantWithImage
            lifecycleScope.launch {
                viewModel.addToCart(productVariant, productResponse.product.promotion_price).await().also { message ->
                    it.snackbar(message)
//                    Toast.makeText(this@DetailFragment.requireContext(), message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailBinding = FragmentDetailBinding.inflate(inflater,container,false)

    override fun getViewModel() = ProductDetailViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor : NetworkConnectionInterceptor) =
            ProductDetailRepository(remoteDataSource.buildApi(ProductApi::class.java, networkConnectionInterceptor), db)

    companion object {
        private val TAG = "DetailFragment"
    }
}