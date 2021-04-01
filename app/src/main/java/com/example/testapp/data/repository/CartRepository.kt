package com.example.testapp.data.repository

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.fullmvvm.util.Coroutines
import com.example.testapp.Constant
import com.example.testapp.Constant.NODE_IMAGES
import com.example.testapp.Constant.NODE_PRODUCTS
import com.example.testapp.Constant.NODE_PRODUCT_VARIANTS
import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.db.entities.*
import com.example.testapp.data.network.ProductApi
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(
    private val database: AppDatabase,
    private val api: ProductApi
):BaseRepository() {
    private lateinit var cart: Cart

    suspend fun getCartFromDatabase(): Cart {
        return withContext(Dispatchers.IO){
            database.getCartDao().getCart()
        }
    }

    suspend fun removeCartItem(item: ProductVariant): String {
        return withContext(Dispatchers.IO) {
            try {
                    cart = database.getCartDao().getCart()
                    cart.removeItemFromCart(item)
                    if(cart.totalPrice <= 0 || cart.totalQty <= 0)
                        database.getCartDao().deleteCart(cart)
                    else
                        database.getCartDao().insertCart(cart)
                    DELETED
            } catch (e: Exception) {
                e.toString()
            }
        }
    }

    suspend fun getSelectedAddress(): AddressCustomer {
        return withContext(Dispatchers.IO){
            database.getAddressDao().getSelectedAddress()
        }
    }

    suspend fun checkOut(cartAndAddress: CartAndAddress) =
            safeApiCall { api.postCheckOut(cartAndAddress) }

    suspend fun removeCart(cart: Cart) {
        withContext(Dispatchers.IO){
            database.getCartDao().deleteCart(cart)
        }
    }

    //Firebase Database
    fun loadImageToView(imageID: Int, imageView: ImageView) {
        firebaseDatabase.getReference(NODE_IMAGES)
                .orderByChild("id")
                .equalTo(imageID.toDouble())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            snapshot.children.forEach {
                                it.getValue(Image::class.java)?.let { image ->
                                    getProductImageFromFirebase(image.link).addOnSuccessListener { uri ->
                                        Picasso.get().load(uri).into(imageView)
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG,"Error: $error")
                    }

                })
    }

    fun loadProductNameToTextView(productID: Int, textView: TextView) {
        getProductFromFirebase(productID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    if (snapshot.exists()) {
                        snapshot.children.forEach {
                            it.getValue(Product::class.java)?.let { product ->
                                textView.text = product.name
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG,"Error: $error")
            }

        })
    }

    fun getProductFromFirebase(productID: Int) =
            firebaseDatabase.getReference(NODE_PRODUCTS).orderByChild("id").equalTo(productID.toDouble())

    private fun getProductVariant() = firebaseDatabase.getReference(NODE_PRODUCT_VARIANTS)

    fun checkOut(cart: Cart, addressCustomer: AddressCustomer) {
        getProductVariant().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.children.map {
                        it.getValue(ProductVariant::class.java)
                    }.let {

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG,"Error: $error")
            }

        })

    }

    fun getCustomers() = firebaseDatabase.getReference(Constant.NODE_CUSTOMERS).orderByChild("id")

    fun getBills() = firebaseDatabase.getReference(Constant.NODE_BILLS).orderByChild("id")

    fun getProductVariants() = firebaseDatabase.getReference(NODE_PRODUCT_VARIANTS).orderByChild("id")

    fun getBillDetails() = firebaseDatabase.getReference(Constant.NODE_BILL_DETAILS).orderByChild("id")

    fun getProduct(productID: Int) = getProductFromFirebase(productID)

    companion object {
        private const val DELETED = "Đã Xóa"
        private const val TAG = "CartRepository"
    }
}