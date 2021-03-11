package com.example.testapp.ui.product

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fullmvvm.util.ApiException
import com.example.fullmvvm.util.Coroutines
import com.example.fullmvvm.util.NoInternetException
import com.example.fullmvvm.util.lazyDeferred
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.ProductRepository
import com.example.testapp.data.response.ProductResponse
import com.example.testapp.ui.base.BaseViewModel
import com.example.testapp.ui.visible
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProductViewModel(
    private val reposiroty: ProductRepository
):BaseViewModel(reposiroty) {
    val productFromRoom by lazyDeferred {
            reposiroty.getProductFromRoom()
    }

    private val _products : MutableLiveData<Resource<ProductResponse>> = MutableLiveData()
    val products : LiveData<Resource<ProductResponse>>
        get() = _products

    init {
        getProducts()
    }

    fun getProducts() = viewModelScope.launch {
        _products.value = Resource.Loading
        _products.value = reposiroty.getProduct()
    }

//    fun saveCart(id: Int) {
//
//    }
}