package com.example.testapp.ui.cart.checkout.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.testapp.data.db.entities.AddressCustomer
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.repository.AddressRepository
import com.example.testapp.databinding.AddAddressFragmentBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.product.detail.DetailFragmentArgs
import kotlinx.coroutines.launch

class EditAddressFragment : BaseFragment<AddressViewModel, AddAddressFragmentBinding, AddressRepository>() {

    val safeArgs: EditAddressFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.getEditAddress(safeArgs.id).await().also {
                binding.editTextCustomerName.setText(it.name)
                binding.editTextCustomerAddress.setText(it.address)
                binding.editTextCustomerEmail.setText(it.email)
                binding.editTextCustomerPhone.setText(it.phone)
                when(it.gender){
                    "Nam" -> binding.radioButtonMale.isChecked=true
                    "Nữ"  -> binding.radioButtonFemale.isChecked = true
                }
                binding.buttonSaveAddress.setOnClickListener { v->
                    val gender = when(binding.radioGroupCustomerGender.checkedRadioButtonId){
                        binding.radioButtonMale.id -> "Nam"
                        binding.radioButtonFemale.id -> "Nữ"
                        else -> "Khác"
                    }
                    val addressCustomer = AddressCustomer(it.id,
                            binding.editTextCustomerName.text.toString(),
                            binding.editTextCustomerEmail.text.toString(),
                            it.selected,
                            binding.editTextCustomerPhone.text.toString(),
                            binding.editTextCustomerAddress.text.toString(),
                            gender
                    )
                    viewModel.updateAddress(addressCustomer)
                    v.findNavController().navigateUp()
                }
            }
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AddAddressFragmentBinding = AddAddressFragmentBinding.inflate(inflater, container, false)

    override fun getViewModel() = AddressViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): AddressRepository =
        AddressRepository(db)

}