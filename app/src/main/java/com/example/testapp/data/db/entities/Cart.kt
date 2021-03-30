package com.example.testapp.data.db.entities

import android.os.Parcel
import android.os.Parcelable
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
        var totalPrice: Int
): Parcelable
{
    fun addToCart(item: ProductVariantWithImage, promotionPrice:Int, qty: Int) {
        val price = (item.productVariant.unit_price * (100 - promotionPrice)) / 100
        var cartItem = CartItem(item, 0, item.productVariant.unit_price.toInt(), 0)
        this.items?.let { it ->
            it.cartItems.find { cartItem ->
                cartItem.item.productVariant.id == item.productVariant.id
            }.let HasItem@{
                it?.let {
                    cartItem = it
                    cartItem.quantity += qty
                    cartItem.totalPriceItem += cartItem.price * cartItem.quantity
                    return@HasItem
                }
                //if cart doesn't has this product
                cartItem = CartItem(item, 1, price.toInt(), price.toInt())
                this.items?.cartItems?.add(cartItem)
            }
            this.id=0
            this.totalQty += qty
            this.totalPrice += cartItem.price * qty
            return
        }
        //if cart has nothing
        this.id = 0
        this.totalQty += qty
        this.totalPrice += cartItem.price * qty
        cartItem.quantity += qty
        cartItem.totalPriceItem = cartItem.price * cartItem.quantity
        this.items = CartItems(mutableListOf(cartItem))
    }

    fun removeItemFromCart(item: ProductVariantWithImage) {
        this.items?.let { items ->
            items.cartItems.find { it.item.productVariant.id==item.productVariant.id }.let { item ->
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