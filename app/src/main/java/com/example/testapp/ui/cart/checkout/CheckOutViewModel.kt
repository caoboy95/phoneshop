package com.example.testapp.ui.cart.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.data.db.entities.AddressCustomer
import com.example.testapp.data.db.entities.Cart
import com.example.testapp.data.db.entities.CartAndAddress
import com.example.testapp.data.repository.CheckOutRepository
import com.example.testapp.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CheckOutViewModel(
    private val repository: CheckOutRepository
) : BaseViewModel(repository) {
    private val _cart : MutableLiveData<Cart> = MutableLiveData()
    val cart: LiveData<Cart>
        get()= _cart

    private val _address: MutableLiveData<AddressCustomer> = MutableLiveData()
    val address: LiveData<AddressCustomer>
        get()= _address

    init {
        getCartFromDatabase()
    }

    fun getCartFromDatabase(){
        viewModelScope.launch {
            _cart.postValue(repository.getCartFromDatabase())
        }
    }

    fun getSelectedAddress(){
        viewModelScope.launch {
            _address.postValue(repository.getSelectedAddress())
        }
    }

    fun checkOut(cartAndAddress: CartAndAddress) = viewModelScope.async { repository.checkOut(cartAndAddress) }

    fun removeCart(cart: Cart){
        viewModelScope.launch {
            repository.removeCart(cart)
        }
    }
}