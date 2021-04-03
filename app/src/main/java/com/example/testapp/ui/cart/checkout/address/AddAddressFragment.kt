package com.example.testapp.ui.cart.checkout.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
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
            if (address.address.isNullOrBlank() || address.email.isNullOrBlank() || address.name.isNullOrBlank() || address.phone.isNullOrBlank()) {
                this.view?.snackbar("Giá trị không được để trống")
                return@setOnClickListener
            }
            viewModel.addAddress(address)
            this.view?.snackbar("Thêm Thành Công")
            this.view?.findNavController()?.navigateUp()
        }
        setFocusEditText()
    }

    fun setFocusEditText() {
        val focusListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!hasFocus) {
                    v?.let {
                        hideKeyboard(v)
                    }
                }
            }
        }
        binding.editTextCustomerAddress.setOnFocusChangeListener(focusListener)
        binding.editTextCustomerEmail.setOnFocusChangeListener(focusListener)
        binding.editTextCustomerName.setOnFocusChangeListener(focusListener)
        binding.editTextCustomerPhone.setOnFocusChangeListener(focusListener)
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager =
            ContextCompat.getSystemService(this.requireContext(), InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): AddAddressFragmentBinding = AddAddressFragmentBinding.inflate(inflater,container,false)

    override fun getViewModel()= AddressViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): AddressRepository =
        AddressRepository(db)

}