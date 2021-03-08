package com.example.testapp.ui.cart.checkout

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.R
import com.example.testapp.data.db.entities.AddressCustomer
import com.example.testapp.data.db.entities.Cart
import com.example.testapp.data.db.entities.CartAndAddress
import com.example.testapp.data.db.entities.CartItem
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.CheckOutRepository
import com.example.testapp.databinding.AlertCheckoutLayoutBinding
import com.example.testapp.databinding.CheckOutFragmentBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.snackbar
import com.example.testapp.ui.visible
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class CheckOutFragment : BaseFragment<CheckOutViewModel, CheckOutFragmentBinding, CheckOutRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSelectedAddress()
        viewModel.cart.observe(viewLifecycleOwner, Observer {
            viewModel.address.observe(viewLifecycleOwner, Observer { address->
                if(address!=null) {
                    updateAddressUI(address)
                    setAlertDialog(it, address)
                }
                else
                    this.view?.snackbar("Nhập địa chỉ!")
            })
            if (it != null)
                updateUI(it)
            else
                refreshUI()
        })
    }

    fun refreshUI(){
        binding.recyclerViewCartItemInfo.visible(false)
        binding.textViewTotalPrice.text= NumberFormat.getCurrencyInstance(Locale("vn", "VN")).format(0)
    }

    fun updateAddressUI(address: AddressCustomer){
        binding.layoutAddressInfo.address=address
    }

    fun setAlertDialog(cart: Cart,address: AddressCustomer){
        binding.buttonOrder.setOnClickListener {
            val builder: AlertDialog.Builder? = activity?.let {
                AlertDialog.Builder(it)
            }
            val inflater = requireActivity().layoutInflater
            val alertBinding: AlertCheckoutLayoutBinding = DataBindingUtil.inflate(inflater,R.layout.alert_checkout_layout,requireView().parent as ViewGroup,false)
            alertBinding.textViewTotal.text= NumberFormat.getCurrencyInstance(Locale("vn", "VN")).format(cart.totalPrice)
            alertBinding.textViewName.text= address.name
            alertBinding.textViewAddress.text= address.address
            alertBinding.textViewEmail.text = address.email
            alertBinding.textViewPhone.text = address.phone
            alertBinding.textViewGender.text = address.gender
            alertBinding.textViewQuantity.text = cart.totalQty.toString()
            builder?.setView(alertBinding.root)
                    ?.setPositiveButton("OK"
                    ) { _, _ ->
                        lifecycleScope.launch {
                            viewModel.checkOut(CartAndAddress(cart, address)).await().also {
                                when (it) {
                                    is Resource.Success -> {
                                        if (it.value.isSuccessful) {
                                            this@CheckOutFragment.view?.snackbar(it.value.checkOutMessage)
                                            viewModel.removeCart(cart)
                                            val action = CheckOutFragmentDirections.actionCheckOutFragmentToCheckOutNotifyFragment(it.value.idBill,CartAndAddress(cart, address))
                                            this@CheckOutFragment.findNavController().navigate(action)
                                        } else {
                                            this@CheckOutFragment.view?.snackbar(it.value.checkOutMessage)
                                            this@CheckOutFragment.view?.findNavController()?.navigateUp()
                                        }
                                    }
                                    is Resource.Failure -> this@CheckOutFragment.view?.snackbar(it.message + " 1")
                                }
                            }
                        }
                    }
                    ?.setNegativeButton("Cancel"
                    ) { dialog, _ ->
                        dialog.cancel()
                    }
                    ?.setTitle("Hãy kiểm tra thông tin trước khi đặt hàng!")
            if (builder != null) {
                builder.create()
                builder.show()
            }
        }
    }

    fun updateUI(cart: Cart){
        cart.items?.let {
            initRecyclerView(it.cartItems as List<CartItem>)
        }
        binding.textViewTotalPrice.text= NumberFormat.getCurrencyInstance(Locale("vn", "VN")).format(cart.totalPrice)
        binding.buttonEditShippingInfo.setOnClickListener {
            it.findNavController().navigate(CheckOutFragmentDirections.actionCheckOutFragmentToAddressFragment())
        }
    }

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): CheckOutFragmentBinding = CheckOutFragmentBinding.inflate(inflater, container, false)

    override fun getViewModel() = CheckOutViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor)= CheckOutRepository(db,remoteDataSource.buildApi(ProductApi::class.java,networkConnectionInterceptor))

    fun initRecyclerView(cart: List<CartItem>){
        val mAdapter = CartItemAdapter()
        mAdapter.setData(cart)
        binding.recyclerViewCartItemInfo.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter= mAdapter
        }
    }
}