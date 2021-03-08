package com.example.testapp.data.db

import androidx.room.*
import com.example.testapp.data.db.entities.AddressCustomer

@Dao
interface AddressDao {
    @Query("SELECT * FROM AddressCustomer")
    fun getAddress(): List<AddressCustomer>

    @Insert
    fun insertAddress(address: AddressCustomer)

    @Delete
    fun deleteCart(address: AddressCustomer)

    @Query("UPDATE AddressCustomer SET selected = 0")
    fun unselectAllAddress()

    @Query("UPDATE AddressCustomer SET selected = 1 WHERE id = :id")
    fun selectAddress(id: Int)

    @Query("SELECT * FROM AddressCustomer WHERE selected = 1")
    fun getSelectedAddress(): AddressCustomer

    @Query("SELECT * FROM AddressCustomer WHERE id = :id")
    fun getEditAddress(id: Int): AddressCustomer

    @Update
    fun updateAddress(address: AddressCustomer)
}