package com.example.testapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fullmvvm.util.Coroutines
import com.example.testapp.Constant.NODE_COMPANIES
import com.example.testapp.Constant.NODE_IMAGES
import com.example.testapp.Constant.NODE_PRODUCTS
import com.example.testapp.Constant.NODE_TYPE_PRODUCTS
import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.network.Resource
import com.example.testapp.data.response.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.simplifiedcoding.mvvmsampleapp.data.preferences.PreferenceProvider
import org.threeten.bp.LocalDateTime


private val MINIMUM_INTERVAL = 6

class ProductRepository(
    private val api: ProductApi,
    private val database: AppDatabase,
    private val prefs: PreferenceProvider
):BaseRepository() {
    private val products = MutableLiveData<List<Product>>()

    //Firebase
    fun getBrandFromFirebase(brandID: Int) = firebaseDatabase.getReference(NODE_COMPANIES).orderByChild("id").equalTo(brandID.toDouble())

    fun getTypesFromFirebase() = firebaseDatabase.getReference(NODE_TYPE_PRODUCTS).orderByChild("id")

    fun getImagesFromFirebase() = firebaseDatabase.getReference(NODE_IMAGES).orderByChild("id")

    fun getProductsFromFirebase() = firebaseDatabase.getReference(NODE_PRODUCTS).orderByChild("id")


    //Rest API
    suspend fun getProduct(): Resource<ProductResponse> {
        val response : Resource<ProductResponse> = safeApiCall { api.getProduct() }
        if(response is Resource.Success)
            products.postValue(response.value.products)
        return response
    }

    // Room Database
    suspend fun getProductFromRoom() : LiveData<List<Product>> {
        return withContext(Dispatchers.IO) {
            database.getProductDao().getProducts()
        }
    }

    private fun saveProducts(products: List<Product>) {
        Coroutines.io {
            prefs.savelastSavedAt(LocalDateTime.now().toString())
            database.getProductDao().saveAllProducts(products)
        }
    }

}