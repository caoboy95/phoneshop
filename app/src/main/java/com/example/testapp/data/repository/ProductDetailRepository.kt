package com.example.testapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.testapp.Constant.NODE_PRODUCTS
import com.example.testapp.Constant.NODE_PRODUCT_VARIANTS
import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.db.entities.Cart
import com.example.testapp.data.db.entities.Product
import com.example.testapp.data.db.entities.ProductVariant
import com.example.testapp.data.db.entities.ProductVariantWithImage
import com.example.testapp.data.network.ProductApi
import com.google.firebase.database.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductDetailRepository(
        private val api: ProductApi,
        private val db: AppDatabase
): BaseRepository() {
    private lateinit var cart: Cart

    suspend fun getProductDetailFromRoom(id: Int) : LiveData<Product> {
        return withContext(Dispatchers.IO) {
            db.getProductDao().getProductItem(id)
        }
    }

    suspend fun getProductDetailFromApi(id: Int) = safeApiCall { api.getProductDetail(id) }

    suspend fun getProductVariantsFromApi(id_product: Int) = safeApiCall { api.getProductVariants(id_product) }

    suspend fun addToCart(productVariant: ProductVariant, promotionPrice: Int) :String {
        return withContext(Dispatchers.IO) {
            try {
                if(productVariant.quantity != 0) {
                    cart = if (db.getCartDao().getCart() != null) {
                        db.getCartDao().getCart()
                    } else {
                        Cart(0, null, 0, 0)
                    }
                    cart.addToCart(productVariant, promotionPrice, 1)
                    db.getCartDao().insertCart(cart)
                    return@withContext ADDED_CART
                }
                OUT_OF_STOCK
            } catch (e: Exception) {
                e.toString()
            }
        }
    }

    fun getProductVariantsFromFirebase(productID: Int) : Query {
        return firebaseDatabase.getReference(NODE_PRODUCT_VARIANTS).orderByChild("id_product").equalTo(productID.toDouble())
    }

    companion object {
        private const val ADDED_CART = "Đã Thêm Vào Giỏ Hàng"
        private const val OUT_OF_STOCK = "Sản Phẩm Đã Hết Hàng"
        private const val TAG = "ProductDetailRepo"
    }
}