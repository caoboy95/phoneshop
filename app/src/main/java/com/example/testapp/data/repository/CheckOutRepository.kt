package com.example.testapp.data.repository

import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.db.entities.AddressCustomer
import com.example.testapp.data.db.entities.Cart
import com.example.testapp.data.db.entities.CartAndAddress
import com.example.testapp.data.network.ProductApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CheckOutRepository(
    private val db: AppDatabase,
    private val api: ProductApi
): BaseRepository() {
    suspend fun getCartFromDatabase(): Cart {
        return withContext(Dispatchers.IO){
            db.getCartDao().getCart()
        }
    }

    suspend fun getSelectedAddress(): AddressCustomer {
        return withContext(Dispatchers.IO){
            db.getAddressDao().getSelectedAddress()
        }
    }

    suspend fun checkOut(cartAndAddress: CartAndAddress) =
            safeApiCall { api.postCheckOut(cartAndAddress) }

    suspend fun removeCart(cart: Cart) {
        withContext(Dispatchers.IO){
            db.getCartDao().deleteCart(cart)
        }
    }
}