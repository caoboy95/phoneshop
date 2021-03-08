package com.example.testapp.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.data.db.entities.Cart
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.data.repository.CartRepository
import com.example.testapp.ui.base.BaseViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository
) : BaseViewModel(repository) {
    private val _cart : MutableLiveData<Cart> = MutableLiveData()
    val cart: LiveData<Cart>
        get()= _cart

    init {
        getCartFromDatabase()
    }

    fun getCartFromDatabase(){
        viewModelScope.launch {
            _cart.postValue(repository.getCartFromDatabase())
        }
    }

    fun removeItemFromCart(item: ProductVariantWithImage): Deferred<String> {
        return viewModelScope.async {
            val message = repository.removeCartItem(item)
            getCartFromDatabase()
            message
        }
    }
}