package com.example.testapp.ui.product

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fullmvvm.util.ApiException
import com.example.fullmvvm.util.Coroutines
import com.example.fullmvvm.util.NoInternetException
import com.example.fullmvvm.util.lazyDeferred
import com.example.testapp.Constant
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.ProductRepository
import com.example.testapp.data.response.ProductResponse
import com.example.testapp.ui.base.BaseViewModel
import com.example.testapp.ui.visible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ProductViewModel(
    private val reposiroty: ProductRepository
):BaseViewModel(reposiroty) {
    val productFromRoom by lazyDeferred {
            reposiroty.getProductFromRoom()
    }
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _products : MutableLiveData<Resource<ProductResponse>> = MutableLiveData()
    val products : LiveData<Resource<ProductResponse>>
        get() = _products

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?>
        get() = _result

    init {
        getProductsFromFirebase()
    }

    fun getProducts() = viewModelScope.launch {
        _products.value = Resource.Loading
        _products.value = reposiroty.getProduct()
    }

    fun addAllProductsToFirebase(products: List<Product>) {
        val dbProduct = firebaseDatabase.getReference(Constant.NODE_PRODUCTS)

        products.forEach{
            dbProduct.child(it.id.toString()).setValue(it)
                .addOnCompleteListener { ex ->
                    if (ex.isSuccessful) {
                        _result.value = null
                    } else {
                        _result.value = ex.exception
                    }
                }
        }
    }
    fun getProductsFromFirebase() {
        val dbProduct = firebaseDatabase.getReference(Constant.NODE_PRODUCTS)
        dbProduct.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val products = mutableListOf<Product>()
                    snapshot.children.forEach {
                        it?.let { dataSnapshot ->
                            val product = dataSnapshot.getValue(Product::class.java)
                            product?.id = dataSnapshot.key?.toInt()!!
                            product?.let { product -> products.add(product) }
                        }
                    }
                    _products.value = Resource.Success(ProductResponse(products))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _result.value = error.toException()
            }

        })
    }

//    fun saveCart(id: Int) {
//
//    }
}