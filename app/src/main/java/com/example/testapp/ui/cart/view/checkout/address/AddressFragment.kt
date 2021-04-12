package com.example.testapp.ui.cart.view.checkout.address

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.R
import com.example.testapp.data.db.entities.AddressCustomer
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.repository.AddressRepository
import com.example.testapp.databinding.AddressFragmentBinding
import com.example.testapp.databinding.AddressInfoAdapterBinding
import com.example.testapp.databinding.AlertCheckoutLayoutBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.cart.adapter.AddressInfoAdapter
import com.example.testapp.ui.cart.view.checkout.CheckOutFragment
import com.example.testapp.ui.cart.viewmodel.AddressViewModel
import com.example.testapp.ui.formatCurrency
import com.example.testapp.ui.visible

class AddressFragment : BaseFragment<AddressViewModel,AddressFragmentBinding,AddressRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.getAddress()
        viewModel.address.observe(viewLifecycleOwner, Observer {
            it?.let { updateUI(it) }
        })
    }

    private fun updateUI(address: List<AddressCustomer>) {
        initRecyclerView(address)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_address_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_item_add_address -> this.view?.findNavController()?.navigate(AddressFragmentDirections.actionAddressFragmentToAddAddressFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView(address: List<AddressCustomer>) {
        val mAdapter = AddressInfoAdapter()
        mAdapter.setData(address)
        mAdapter.setOnClickListener(object : AddressInfoAdapter.AddressClickListener {
            override fun onEditClickListener(id: Int) {
                this@AddressFragment.view?.findNavController()?.navigate(AddressFragmentDirections.actionAddressFragmentToEditAddressFragment(id))
            }

            override fun onRemoveClickListener(addressCustomer: AddressCustomer) {
                setAlertRemove(addressCustomer)
            }

            override fun onSelectedClickListener(id: Int) {
                viewModel.selectAddress(id)
                this@AddressFragment.view?.findNavController()?.navigateUp()
            }
        })
        binding.recyclerViewAddresses.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private fun setAlertRemove(addressCustomer: AddressCustomer) {
        val alertBuilder = activity?.let {
            AlertDialog.Builder(it)
        }
        val inflater = requireActivity().layoutInflater
        val alertBinding: AlertCheckoutLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.alert_checkout_layout, requireView().parent as ViewGroup,false)
        alertBinding.textViewName.text = addressCustomer.name
        alertBinding.textViewAddress.text = addressCustomer.address
        alertBinding.textViewEmail.text = addressCustomer.email
        alertBinding.textViewPhone.text = addressCustomer.phone
        alertBinding.textViewGender.text = addressCustomer.gender
        alertBinding.textViewQuantity.visible(false)
        alertBinding.textViewTotal.visible(false)
        alertBinding.textView8.visible(false)
        alertBinding.textView9.visible(false)
        alertBinding.textViewAlertCheckoutTitle.visible(false)
        alertBuilder?.apply {
            setView(alertBinding.root)
            setPositiveButton("OK") { _, _ ->
                viewModel.removeAddress(addressCustomer)
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            setTitle("Bạn có chắc chắn xóa địa chỉ: ")
            create()
            show()
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AddressFragmentBinding =
        AddressFragmentBinding.inflate(inflater,container,false)

    override fun getViewModel() = AddressViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): AddressRepository =
        AddressRepository(appDatabase)

}