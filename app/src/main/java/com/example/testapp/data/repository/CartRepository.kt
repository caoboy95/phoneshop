package com.example.testapp.data.repository

import com.example.fullmvvm.util.Coroutines
import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.db.entities.Cart
import com.example.testapp.data.db.entities.CartItem
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.data.network.ProductApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(
    private val db: AppDatabase
):BaseRepository(){

    private lateinit var cart: Cart

    suspend fun getCartFromDatabase(): Cart {
        return withContext(Dispatchers.IO){
            db.getCartDao().getCart()
        }
    }

    suspend fun removeCartItem(item: ProductVariantWithImage): String {
        return withContext(Dispatchers.IO) {
            try {
                    cart = db.getCartDao().getCart()
                    cart.removeItemFromCart(item)
                    if(cart.totalPrice<=0 || cart.totalQty<=0)
                        db.getCartDao().deleteCart(cart)
                    else
                        db.getCartDao().insertCart(cart)
                "Đã Xóa"
            } catch (e: Exception) {
                e.toString()
            }
        }
    }
}