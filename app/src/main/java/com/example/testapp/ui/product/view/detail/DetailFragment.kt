package com.example.testapp.ui.product.view.detail

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testapp.Constant.SELECTED_VARIANT_KEY
import com.example.testapp.R
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.repository.ProductDetailRepository
import com.example.testapp.databinding.FragmentDetailBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.product.adapter.DetailProductPagerAdapter
import com.example.testapp.ui.product.viewmodel.ProductDetailViewModel
import com.example.testapp.ui.snackbar
import com.example.testapp.ui.visible
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch


class DetailFragment : BaseFragment<ProductDetailViewModel, FragmentDetailBinding, ProductDetailRepository>() {
    private var pagerAdapter : DetailProductPagerAdapter? = null
    private val safeArgs: DetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.setData(safeArgs.product)
        binding.progressBar.visible(true)
//        viewModel.getProductDetailFromApi(viewModel.productID)
        viewModel.productVariantsFB.observe(viewLifecycleOwner, Observer {
            it?.let {
                updateUI(safeArgs.product)
                addViewPagerControl(safeArgs.product, requireContext(), it)
                binding.progressBar.visible(false)
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

    private fun addViewPagerControl(product : Product, context: Context, productVariants: List<ProductVariant>) {
        val manager :FragmentManager = childFragmentManager
        pagerAdapter = DetailProductPagerAdapter(manager, context, product)
        pagerAdapter?.setData(productVariants)
        binding.pager.adapter = pagerAdapter
        binding.pager.offscreenPageLimit = 1
        binding.tabLayout.setupWithViewPager(binding.pager)
    }

    private fun updateUI(product: Product) {
        viewModel.getRepository().getProductImageFromFirebase(product.image).addOnSuccessListener {
            Picasso.get().load(it).placeholder(R.drawable.placeholder).into(binding.imageViewProductImage)
        }
        binding.buttonAddToCart.setOnClickListener {
            val productVariant = this.arguments?.getParcelable<ProductVariant>(SELECTED_VARIANT_KEY) as ProductVariant
            lifecycleScope.launch {
                viewModel.addToCartAsync(productVariant, product.promotion_price).await().also { message ->
                    it.snackbar(message)
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
            ProductDetailRepository(remoteDataSource.buildApi(ProductApi::class.java, networkConnectionInterceptor), appDatabase)

    companion object {
        private const val TAG = "DetailFragment"
    }
}