package com.example.testapp.ui.cart.viewmodel

import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.R
import com.example.testapp.data.db.entities.Cart
import com.example.testapp.data.db.entities.Image
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.data.repository.CartRepository
import com.example.testapp.ui.base.BaseViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CartViewModel(
    private val repository: CartRepository
) : BaseViewModel(repository) {
    private val _cart : MutableLiveData<Cart> = MutableLiveData()
    val cart: LiveData<Cart>
        get() = _cart

    init {
        getCartFromDatabase()
    }

    private fun getCartFromDatabase() {
        viewModelScope.launch {
            _cart.postValue(repository.getCartFromDatabase())
        }
    }

    fun removeItemFromCartAsync(item: ProductVariant): Deferred<String> {
        return viewModelScope.async {
            val message = repository.removeCartItem(item)
            getCartFromDatabase()
            message
        }
    }

    fun getRepository() = repository
}