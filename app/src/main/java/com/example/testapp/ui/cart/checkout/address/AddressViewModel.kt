package com.example.testapp.ui.cart.checkout.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.data.db.entities.AddressCustomer
import com.example.testapp.data.repository.AddressRepository
import com.example.testapp.ui.base.BaseViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

class AddressViewModel(
    private val repository: AddressRepository
) : BaseViewModel(repository){
    private val _address: MutableLiveData<List<AddressCustomer>> = MutableLiveData()
    val address: LiveData<List<AddressCustomer>>
        get() = _address

    init {
        getAddress()
    }

    fun getAddress(){
        viewModelScope.launch {
            _address.postValue(repository.getAddress())
        }
    }

    fun addAddress(address: AddressCustomer){
        viewModelScope.launch {
            repository.addAddress(address)
            _address.postValue(repository.getAddress())
        }
    }
    fun removeAddress(address: AddressCustomer){
        viewModelScope.launch {
            repository.removeAddress(address)
            _address.postValue(repository.getAddress())
        }
    }

    fun selectAddress(id: Int){
        viewModelScope.launch {
            repository.changeSelectedAddress(id)
            _address.postValue(repository.getAddress())
        }
    }

    fun getEditAddress(id: Int): Deferred<AddressCustomer>{
        return viewModelScope.async {
            repository.getEditAddress(id)
        }
    }

    fun updateAddress(addressCustomer: AddressCustomer){
        viewModelScope.launch {
            repository.updateAddress(addressCustomer)
        }
    }
}