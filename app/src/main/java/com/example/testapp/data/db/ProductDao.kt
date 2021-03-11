package com.example.testapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testapp.data.db.entities.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM Product")
    fun getProducts(): LiveData<List<Product>>

    @Query("SELECT * FROM Product WHERE id = :id")
    fun getProductItem(id: Int): LiveData<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllProducts(products : List<Product>)

}