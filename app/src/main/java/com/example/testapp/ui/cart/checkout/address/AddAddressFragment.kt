package com.example.testapp.ui.cart.checkout.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.testapp.data.db.entities.AddressCustomer
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.repository.AddressRepository
import com.example.testapp.databinding.AddAddressFragmentBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.snackbar


class AddAddressFragment : BaseFragment<AddressViewModel,AddAddressFragmentBinding,AddressRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSaveAddress.setOnClickListener {
            val gender = when(binding.radioGroupCustomerGender.checkedRadioButtonId)
            {
                binding.radioButtonMale.id -> "Nam"
                binding.radioButtonFemale.id -> "Nữ"
                else -> "Không"
            }
            val address = AddressCustomer(0,binding.editTextCustomerName.text.toString(),
                binding.editTextCustomerEmail.text.toString(),
                false,
                binding.editTextCustomerPhone.text.toString(),
                binding.editTextCustomerAddress.text.toString(),
                gender
            )
            viewModel.addAddress(address)
            this.view?.snackbar("Thêm Thành Công")
            this.view?.findNavController()?.navigateUp()
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AddAddressFragmentBinding = AddAddressFragmentBinding.inflate(inflater,container,false)

    override fun getViewModel()= AddressViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): AddressRepository =
        AddressRepository(db)

}