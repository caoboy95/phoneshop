package com.example.testapp.ui.cart.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.data.db.entities.Cart
import com.example.testapp.data.db.entities.CartItem
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.repository.CartRepository
import com.example.testapp.databinding.CartFragmentBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.cart.adapter.CartViewAdapter
import com.example.testapp.ui.cart.viewmodel.CartViewModel
import com.example.testapp.ui.formatCurrency
import com.example.testapp.ui.snackbar
import com.example.testapp.ui.visible
import kotlinx.coroutines.launch

class CartFragment : BaseFragment<CartViewModel, CartFragmentBinding, CartRepository>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.cart.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                updateUI(it)
            }
            else{
                refreshUI()
            }
        })
    }

    private fun refreshUI() {
        binding.recyclerViewCartItems.visible(false)
        binding.textViewTotalPrice.text= formatCurrency(0)
        binding.buttonCheckout.setOnClickListener {
            this.view?.snackbar("Hãy Chọn Sản Phẩm!")
        }
    }

    private fun updateUI(cart: Cart) {
        binding.textViewTotalPrice.text = formatCurrency(cart.totalPrice)
        initRecyclerView(cart)
        binding.buttonCheckout.setOnClickListener {
            val action = CartFragmentDirections.actionCartFragmentToCheckOutFragment()
            it.findNavController().navigate(action)
        }
    }

    private fun initRecyclerView(cart: Cart) {
        val mAdapter = CartViewAdapter(viewModel.getRepository())
        mAdapter.setData(cart.items?.cartItems as List<CartItem>)
        mAdapter.setOnRemoveClickListener(object : CartViewAdapter.ClickListener {
            override fun onRemoveClickListener(item: ProductVariant) {
                lifecycleScope.launch {
                    this@CartFragment.view?.snackbar(viewModel.removeItemFromCartAsync(item).await())
                }
            }

        })
        binding.recyclerViewCartItems.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CartFragmentBinding = CartFragmentBinding.inflate(inflater, container, false)

    override fun getViewModel() = CartViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor) =
            CartRepository(appDatabase, remoteDataSource.buildApi(ProductApi::class.java,networkConnectionInterceptor))
}