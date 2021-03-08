package com.example.testapp.ui.product

import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullmvvm.util.Coroutines
import com.example.fullmvvm.util.isVisible
import com.example.fullmvvm.util.snackbar
import com.example.testapp.R
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.ProductRepository
import com.example.testapp.databinding.FragmentProductBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.cart.checkout.address.AddressFragmentDirections
import com.example.testapp.ui.handleApiError
import com.example.testapp.ui.product.detail.ProductItem
import com.example.testapp.ui.visible
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

class ProductFragment : BaseFragment<ProductViewModel,FragmentProductBinding,ProductRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.progressBar.visible(false)
        viewModel.products.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
//                    Toast.makeText(requireContext(), it.value.toString(), Toast.LENGTH_SHORT).show()
                    binding.progressBar.visible(false)
                    bindUI(it.value.products)
                }
                is Resource.Loading -> {
                    binding.progressBar.visible(true)
                }
                is Resource.Failure -> {
                    binding.progressBar.visible(false)
                    this.handleApiError(it)
                    Coroutines.main {
                        viewModel.productFromRoom.await().observe(viewLifecycleOwner, Observer {
                            if(it!=null){
                                bindUI(it)
                            }
                        })
                    }
                }
            }
        })
        activity?.actionBar?.setDisplayHomeAsUpEnabled(false)
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

    fun bindUI(products: List<Product>){
        binding.progressBar.isVisible(false)
//            Toast.makeText(requireContext(), it.size.toString(), Toast.LENGTH_SHORT).show()
        initRecyclerView(products.toProductItem())
        products.toProductItem()
    }
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProductBinding = FragmentProductBinding.inflate(inflater,container,false)

    override fun getViewModel() = ProductViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor : NetworkConnectionInterceptor)= ProductRepository(remoteDataSource.buildApi(ProductApi::class.java,networkConnectionInterceptor),db,prefs)

    private fun initRecyclerView(productItem: List<ProductItem>) {

        val mAdapter = GroupAdapter<ViewHolder>().apply {
            addAll(productItem)
        }

        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }

    }


    private fun List<Product>.toProductItem() : List<ProductItem>{
        return this.map{
            ProductItem(it).also {
                it.setOnProductClickListener(object :ProductItem.ClickListener{
                    override fun onItemClick(v: View, product: Product) {
                        v.snackbar("Nothing")
                    }
                })
            }
        }
    }
}