package com.example.testapp.ui.product.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.testapp.Constant
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.ProductInfoRepository
import com.example.testapp.data.response.ProductVariantResponse
import com.example.testapp.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class InfoViewModel(
    private val repository: ProductInfoRepository
) : BaseViewModel(repository) {
    private val _productVariants : MutableLiveData<Resource<ProductVariantResponse>> = MutableLiveData()
    val productVariant : LiveData<Resource<ProductVariantResponse>>
        get() = _productVariants

    fun getProductVariants(id_product: Int) {
        viewModelScope.launch {
            _productVariants.value = Resource.Loading
            _productVariants.value = repository.getProductVariant(id_product)
        }
    }

    fun getType(id: Int) = viewModelScope.async {
        repository.getType(id)
    }

    fun getBrand(id: Int) = viewModelScope.async {
        repository.getBrand(id)
    }

    fun getTypeFromFirebase(typeID: Int) = repository.getTypeFromFirebase(typeID)

    fun getBrandFromFirebase(brandID: Int) = repository.getBrandFromFirebase(brandID)
}