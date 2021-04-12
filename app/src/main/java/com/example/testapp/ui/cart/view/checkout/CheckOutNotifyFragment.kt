package com.example.testapp.ui.cart.view.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.data.db.entities.CartAndAddress
import com.example.testapp.data.db.entities.CartItem
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.repository.CartRepository
import com.example.testapp.databinding.FragmentCheckOutNotifyBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.cart.adapter.CartItemAdapter
import com.example.testapp.ui.cart.viewmodel.CheckOutViewModel
import com.example.testapp.ui.formatCurrency


class CheckOutNotifyFragment : BaseFragment<CheckOutViewModel, FragmentCheckOutNotifyBinding, CartRepository>() {

    private val safeArgs : CheckOutNotifyFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI(safeArgs.cartAndAddress)
    }

    private fun updateUI(cartAndAddress: CartAndAddress) {
        binding.textViewBillId.text = ("#${safeArgs.idBill}")
        binding.layoutAddressInfo.address = cartAndAddress.addressCustomer
        binding.textViewTotalPrice.text = formatCurrency(cartAndAddress.cart.totalPrice)
        cartAndAddress.cart.items?.let {
            initRecyclerView(it.cartItems as List<CartItem>)
        }
        binding.buttonBack.setOnClickListener {
            this.findNavController().navigateUp()
        }
    }

    private fun initRecyclerView(cart: List<CartItem>) {
        val mAdapter = CartItemAdapter(viewModel.getRepository())
        mAdapter.setData(cart)
        binding.recyclerViewCartItemInfo.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter= mAdapter
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentCheckOutNotifyBinding = FragmentCheckOutNotifyBinding.inflate(inflater, container, false)

    override fun getViewModel() = CheckOutViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): CartRepository =
        CartRepository(appDatabase, remoteDataSource.buildApi(ProductApi::class.java, networkConnectionInterceptor))
}