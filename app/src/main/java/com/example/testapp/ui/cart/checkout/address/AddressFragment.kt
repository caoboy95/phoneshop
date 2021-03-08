package com.example.testapp.ui.cart.checkout.address

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.R
import com.example.testapp.data.db.entities.AddressCustomer
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.repository.AddressRepository
import com.example.testapp.databinding.AddressFragmentBinding
import com.example.testapp.ui.base.BaseFragment

class AddressFragment : BaseFragment<AddressViewModel,AddressFragmentBinding,AddressRepository>(){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel.getAddress()
        viewModel.address.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                updateUI(it)
            }
        })
    }

    fun updateUI(address: List<AddressCustomer>){
        initRecyclerView(address)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_address_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_item_add_address -> this.view?.findNavController()?.navigate(AddressFragmentDirections.actionAddressFragmentToAddAddressFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    fun initRecyclerView(address: List<AddressCustomer>)
    {
        var mAdapter = AddressInfoAdapter()
        mAdapter.setData(address)
        mAdapter.setOnClickListener(object : AddressInfoAdapter.AddressClickListener{
            override fun onEditClickListener(id: Int) {
                this@AddressFragment.view?.findNavController()?.navigate(AddressFragmentDirections.actionAddressFragmentToEditAddressFragment(id))
            }

            override fun onRemoveClickListener(addressCustomer: AddressCustomer) {
                viewModel.removeAddress(addressCustomer)
            }

            override fun onSelectedClickListener(id: Int) {
                viewModel.selectAddress(id)
                this@AddressFragment.view?.findNavController()?.navigateUp()
            }
        })
        binding.recyclerViewAddresses.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
            adapter=mAdapter
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AddressFragmentBinding =
        AddressFragmentBinding.inflate(inflater,container,false)

    override fun getViewModel() = AddressViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): AddressRepository =
        AddressRepository(db)

}