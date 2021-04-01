package com.example.testapp.ui.product.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.R
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.repository.ProductRepository
import com.example.testapp.databinding.FragmentProductBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.product.adapter.ProductAdapter
import com.example.testapp.ui.product.viewmodel.ProductViewModel
import com.example.testapp.ui.snackbar
import com.example.testapp.ui.visible
import com.example.testapp.util.NetworkLiveData

class ProductFragment : BaseFragment<ProductViewModel, FragmentProductBinding, ProductRepository>() {
    private var mAdapter: ProductAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding.progressBar.visible(true)
        if (mAdapter == null) { mAdapter = ProductAdapter(viewModel.getRepository()) }
        initRecyclerView()
        NetworkLiveData.init(requireActivity().application)
        NetworkLiveData.observe(viewLifecycleOwner, Observer {
            if (!it) {
                binding.progressBar.visible(true)
                binding.progressBar.bringToFront()
                this.view?.snackbar("${context?.resources?.getString(R.string.network_connection)}")
            } else {
                viewModel.getProductsFromFirebase()
            }
        })
        viewModel.result.observe(viewLifecycleOwner, Observer {
            this.view?.snackbar(it?.message.toString())
        })
        viewModel.products.observe(viewLifecycleOwner, Observer {
            it?.let { products ->
                binding.progressBar.visible(false)
                mAdapter?.setData(products)
            }
            //From API Localhost
//            when (it) {
//                is Resource.Success -> {
//                    binding.progressBar.visible(false)
//                    bindUI(it.value.products)
//                }
//                is Resource.Loading -> {
//                    binding.progressBar.visible(true)
//                }
//                is Resource.Failure -> {
//                    binding.progressBar.visible(false)
//                    this.handleApiError(it)
//                    Coroutines.main {
//                        viewModel.productFromRoom.await().observe(viewLifecycleOwner, Observer {
//                            if(it != null){
//                                bindUI(it)
//                            }
//                        })
//                    }
//                }
//            }
        })
        activity?.actionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cart_menu, menu)
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
            R.id.menu_item_cart -> this.view?.findNavController()?.navigate(R.id.cartFragment, null, navOptions)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private fun isInternetAvailable(): Boolean {
        var result = false
        val connectivityManager = this.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    else -> false
                }
            }
            return result
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentProductBinding = FragmentProductBinding.inflate(inflater, container, false)

    override fun getViewModel() = ProductViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor : NetworkConnectionInterceptor) =
            ProductRepository(remoteDataSource.buildApi(ProductApi::class.java, networkConnectionInterceptor), db, prefs)
}