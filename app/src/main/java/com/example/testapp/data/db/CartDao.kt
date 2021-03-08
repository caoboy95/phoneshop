package com.example.testapp.data.db

import androidx.room.*
import com.example.testapp.data.db.entities.Cart

@Dao
interface CartDao {
    @Query("SELECT * FROM Cart WHERE id = 0")
    fun getCart(): Cart

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCart(cart: Cart)

    @Delete()
    fun deleteCart(cart: Cart)
}