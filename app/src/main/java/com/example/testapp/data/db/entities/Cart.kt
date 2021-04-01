package com.example.testapp.data.db.entities

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Cart(
        @PrimaryKey(autoGenerate = false)
        var id: Int,
        @ColumnInfo(name = "items")
        @TypeConverters(CartItemsConverters::class)
        var items: CartItems?,
        @ColumnInfo(name = "totalqty")
        var totalQty: Int,
        @ColumnInfo(name = "totalprice")
        var totalPrice: Long
): Parcelable
{
    fun addToCart(item: ProductVariant, promotionPrice:Int, qty: Int) {
        val price = (item.unit_price * (100 - promotionPrice)) / 100
        var cartItem = CartItem(item, 0, price, 0)
        this.items?.let { it ->
            it.cartItems.find { cartItem ->
                cartItem.item.id == item.id
            }.let HasItem@{
                it?.let {
                    cartItem = it
                    cartItem.quantity += qty
                    cartItem.totalPriceItem += cartItem.price * cartItem.quantity
                    return@HasItem
                }
                //if cart doesn't has this product
                cartItem = CartItem(item, 1, price, price)
                this.items?.cartItems?.add(cartItem)
            }
            this.totalQty += qty
            this.totalPrice += cartItem.price * qty
            return
        }
        //if cart has nothing
        this.totalQty += qty
        this.totalPrice += cartItem.price * qty
        cartItem.quantity += qty
        cartItem.totalPriceItem = cartItem.price * cartItem.quantity
        this.items = CartItems(mutableListOf(cartItem))
    }

    fun removeItemFromCart(item: ProductVariant) {
        this.items?.let { items ->
            items.cartItems.find { it.item.id==item.id }.let { item ->
                item?.let {
                    this.totalQty -= it.quantity
                    this.totalPrice -= it.totalPriceItem
                    this.items?.cartItems?.remove(it)
                }
            }
        }
    }
}

// use converter to store data to database as object type
class CartItemsConverters {
        @TypeConverter
        fun fromCartItemsJson(stat: CartItems?): String {
                return Gson().toJson(stat)
        }

        @TypeConverter
        fun toCartItemsList(jsonImages: String): CartItems? {
                val notesType = object : TypeToken<CartItems>() {}.type
                return Gson().fromJson<CartItems>(jsonImages, notesType)
        }
}

@Parcelize
data class CartAndAddress(
        val cart: Cart,
        val addressCustomer: AddressCustomer
): Parcelable