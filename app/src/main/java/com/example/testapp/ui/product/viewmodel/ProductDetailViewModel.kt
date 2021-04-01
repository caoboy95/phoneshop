package com.example.testapp.ui.product.viewmodel

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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.*
import kotlin.properties.Delegates

class ProductDetailViewModel(
    private val repository: ProductDetailRepository
) : BaseViewModel(repository) {

    private val _productVariantsFB : MutableLiveData<List<ProductVariant>> = MutableLiveData()
    val productVariantsFB : LiveData<List<ProductVariant>>
        get() = _productVariantsFB


    private val _productVariants : MutableLiveData<Resource<ProductVariantResponse>> = MutableLiveData()
    val productVariants : LiveData<Resource<ProductVariantResponse>>
        get() = _productVariants

    var product : Product? = null

    fun setData(product: Product) {
        this.product = product
        getProductVariantsFromFirebase(product.id)
    }

    fun getRepository() = repository

    fun getProductVariantsFromApi(productID: Int) {
        viewModelScope.launch {
            _productVariants.value = Resource.Loading
            _productVariants.value = repository.getProductVariantsFromApi(productID)
        }
    }

    fun addToCartAsync(productVariant: ProductVariant, promotionPrice: Int): Deferred<String> {
        return viewModelScope.async {
            repository.addToCart(productVariant, promotionPrice)
        }
    }

    private fun getProductVariantsFromFirebase(productID: Int) {
        repository.getProductVariantsFromFirebase(productID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productVariants = mutableListOf<ProductVariant>()
                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        it.getValue(ProductVariant::class.java)?.let { productVariant ->
                            productVariants.add(productVariant)
                        }
                    }
                }
                _productVariantsFB.value = productVariants
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}