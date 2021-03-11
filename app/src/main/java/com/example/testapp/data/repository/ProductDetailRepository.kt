package com.example.testapp.data.repository

import androidx.lifecycle.LiveData
import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.db.entities.Cart
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.data.network.ProductApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductDetailRepository(
        private val api: ProductApi,
        private val db: AppDatabase
): BaseRepository() {
    private lateinit var cart: Cart

    suspend fun getProductDetailFromRoom(id: Int) :LiveData<Product>{
        return withContext(Dispatchers.IO){
            db.getProductDao().getProductItem(id)
        }
    }

    suspend fun getProductDetailFromApi(id: Int) = safeApiCall { api.getProductDetail(id) }

    suspend fun getProductVariantsFromApi(id_product: Int) = safeApiCall { api.getProductVariants(id_product) }

    suspend fun addToCart(productVariant: ProductVariantWithImage,promotionPrice: Int) :String {
        return withContext(Dispatchers.IO) {
            try {
                if(productVariant.productVariant.quantity!=0) {
                    if (db.getCartDao().getCart() != null) {
                        cart = db.getCartDao().getCart()
                    } else {
                        cart = Cart(0, null, 0, 0)
                    }
                    cart.addToCart(productVariant, promotionPrice, 1)
                    db.getCartDao().insertCart(cart)
                    return@withContext "Đã Thêm Vào Giỏ Hàng"
                }
                "Sản Phẩm Đã Hết Hàng"
            } catch (e: Exception) {
                e.toString()
            }
        }
    }

}