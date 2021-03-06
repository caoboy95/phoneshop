package com.example.testapp.ui.product.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fullmvvm.util.lazyDeferred
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.repository.ProductRepository
import com.example.testapp.ui.base.BaseViewModel
import com.example.testapp.ui.getDataValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import okhttp3.internal.notify

class ProductViewModel(
    private val repository: ProductRepository
): BaseViewModel(repository) {
    //Room
    val productFromRoom by lazyDeferred {
        repository.getProductFromRoom()
    }
    //Firebase
    private val _products : MutableLiveData<List<Product>> = MutableLiveData()
    val products : LiveData<List<Product>>
        get() = _products
    private val _result = MutableLiveData<Exception>()
    val result: LiveData<Exception>
        get() = _result

    //Rest API
//    private val _products : MutableLiveData<Resource<ProductResponse>> = MutableLiveData()
//    val products : LiveData<Resource<ProductResponse>>
//        get() = _products

    //Get products from API
//    fun getProducts() = viewModelScope.launch {
//        _products.value = Resource.Loading
//        _products.value = repository.getProduct()
//    }

//    fun addAllProductsToFirebase(products: List<Product>) {
//        val dbProduct = firebaseDatabase.getReference(Constant.NODE_PRODUCTS)
//        products.forEach{
//            dbProduct.child(it.id.toString()).setValue(it)
//                .addOnCompleteListener { ex ->
//                    if (ex.isSuccessful) {
//                        _result.value = null
//                    } else {
//                        _result.value = ex.exception
//                    }
//                }
//        }
//    }

    fun getRepository() = repository

    fun getProductsFromFirebase() {
        repository.getProductsFromFirebase().addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = snapshot.getDataValue(Product::class.java)

                if (products.isNotEmpty()) {
                    _products.value = products
                    return
                }
                Log.e(TAG, "Products is empty")
            }

            override fun onCancelled(error: DatabaseError) {
                _result.value = error.toException()
                Log.e(TAG, "$error")
            }
        })
    }

    companion object {
        private const val TAG = "ProductViewModel"
    }
}