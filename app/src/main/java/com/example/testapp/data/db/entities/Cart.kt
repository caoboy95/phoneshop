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
):Parcelable
{
//    constructor(cart: Cart) : this(cart.id,cart.items,cart.totalQty,cart.totalPrice)
    fun addToCart(item: ProductVariantWithImage,promotionPrice:Int,qty: Int){
//        val price = (item.productVariant.unit_price*(100-promotionPrice))/100
        val price = item.productVariant.unit_price*80/100
        var cartItem : CartItem
        cartItem = if(promotionPrice == 0)
                        CartItem(item,0,item.productVariant.unit_price.toInt(),0)
                    else CartItem(item,0,price.toInt(),0)
        if(this.items!=null)
        {//if cart has nothing
            this.items?.cartItems?.find {
                it.item.productVariant.id==item.productVariant.id
            }.let {
                if (it!=null) {
                    cartItem = it
                    cartItem.quantity+=qty
                    cartItem.totalPriceItem+= cartItem.price*cartItem.quantity
                }
                else { //if cart doesn't has this product
                    cartItem = CartItem(item,1,price.toInt(),price.toInt())
                    this.items?.cartItems?.add(cartItem)
                }
            }
            this.id=0
            this.totalQty = this.totalQty.plus(qty)
            this.totalPrice = this.totalPrice.plus(cartItem.price*qty)
        }
        else {
            this.id = 0
            this.totalQty = this.totalQty.plus(qty)
            cartItem.quantity += qty
            cartItem.totalPriceItem = cartItem.price * cartItem.quantity
            this.items = CartItems(mutableListOf(cartItem))
//        this.items?.cartItems?.add(cartItem)
            this.totalPrice = this.totalPrice.plus(cartItem.price * qty)
        }

    }
    fun removeItemFromCart(item: ProductVariantWithImage){
        var cartItem : CartItem
        if(this.items!=null)
        {//if cart has nothing
            this.items?.cartItems?.find {
                it.item.productVariant.id==item.productVariant.id
            }.let {
                if (it!=null) {
                    cartItem = it
                    this.totalQty = this.totalQty.minus(cartItem.quantity)
                    this.totalPrice = this.totalPrice.minus(cartItem.totalPriceItem)
                    this.items?.cartItems?.remove(cartItem)
                }
            }
            this.id=0

        }
    }
}

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
        val cart:Cart,
        val addressCustomer: AddressCustomer
):Parcelable