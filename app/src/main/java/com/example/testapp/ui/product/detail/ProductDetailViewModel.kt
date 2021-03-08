package com.example.testapp.ui.product.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fullmvvm.util.Coroutines
import com.example.fullmvvm.util.lazyDeferred
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.ProductDetailRepository
import com.example.testapp.data.response.ProductDetailResponse
import com.example.testapp.data.response.ProductResponse
import com.example.testapp.data.response.ProductVariantResponse
import com.example.testapp.ui.base.BaseViewModel
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class ProductDetailViewModel(
    private val repository: ProductDetailRepository
) : BaseViewModel(repository) {

    private val _product : MutableLiveData<Resource<ProductDetailResponse>> = MutableLiveData()
    val product : LiveData<Resource<ProductDetailResponse>>
        get() =_product

    private val _productVariants : MutableLiveData<Resource<ProductVariantResponse>> = MutableLiveData()
    val productVariants : LiveData<Resource<ProductVariantResponse>>
        get() =_productVariants

    private var _productID by Delegates.notNull<Int>()
    val productID : Int
        get() = _productID

    //    val productFromRoom by lazyDeferred {
//        repository.getProductDetailFromRoom()
//    }
//    fun getProductDetail(id: Int): Deferred<LiveData<Product>>{
//        return viewModelScope.async {
//            repository.getProductDetailFromRoom(id)
//        }
//    }
    fun setID(id: Int){
        _productID=id
    }
    fun getProductDetailFromApi(id: Int) {
        viewModelScope.launch {
            _product.value = Resource.Loading
            _product.value = repository.getProductDetailFromApi(id)
        }
    }

    fun getProductVariantsFromApi(id_product: Int){
        viewModelScope.launch {
            _productVariants.value = Resource.Loading
            _productVariants.value = repository.getProductVariantsFromApi(id_product)
        }
    }
    fun addToCart(productVariant: ProductVariantWithImage,promotionPrice: Int): Deferred<String>{
        return viewModelScope.async {
            repository.addToCart(productVariant,promotionPrice)
        }
    }


}