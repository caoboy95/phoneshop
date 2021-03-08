package com.example.testapp.data.repository

import androidx.lifecycle.*
import com.example.fullmvvm.util.ApiException
import com.example.fullmvvm.util.Coroutines
import com.example.fullmvvm.util.NoInternetException
import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.network.Resource
import com.example.testapp.data.response.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.simplifiedcoding.mvvmsampleapp.data.preferences.PreferenceProvider
import okhttp3.ResponseBody
import org.threeten.bp.LocalDateTime
import org.threeten.bp.temporal.ChronoUnit
import retrofit2.Response

private val MINIMUM_INTERVAL = 6

class ProductRepository(
    private val api: ProductApi,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
):BaseRepository() {

    private val products = MutableLiveData<List<Product>>()

    init {
        products.observeForever {
            saveProducts(it)
        }
    }
    //get data from api
    suspend fun getProduct(): Resource<ProductResponse> {
        var response : Resource<ProductResponse> = safeApiCall { api.getProduct() }
        if(response is Resource.Success)
            products.postValue(response.value.products)
        return response
    }
    // get data from room database
    suspend fun getProductFromRoom() : LiveData<List<Product>> {
        return withContext(Dispatchers.IO) {
//            fetchProducts()
            db.getProductDao().getProducts()
        }
    }
    // save data to room database
    private fun saveProducts(products: List<Product>) {
        Coroutines.io {
            prefs.savelastSavedAt(LocalDateTime.now().toString())
            db.getProductDao().saveAllProducts(products)
        }
    }
//    private suspend fun fetchProducts() {
//        var response : Resource<ProductResponse>? =null
//        val lastSavedAt = prefs.getLastSavedAt()
//        if (lastSavedAt == null || isFetchNeeded(LocalDateTime.parse(lastSavedAt))) {
//                response = getProduct()
//                _products.postValue(response)
//        }
//    }
//
//    private fun isFetchNeeded(savedAt: LocalDateTime): Boolean {
////        return ChronoUnit.HOURS.between(savedAt,LocalDateTime.now()) > MINIMUM_INTERVAL
//        return true
//    }
}