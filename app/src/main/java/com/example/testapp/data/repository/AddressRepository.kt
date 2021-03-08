package com.example.testapp.data.repository


import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.db.entities.AddressCustomer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddressRepository(
    private val db: AppDatabase
): BaseRepository() {
    suspend fun getAddress(): List<AddressCustomer> {
        return withContext(Dispatchers.IO){
            db.getAddressDao().getAddress()
        }
    }

    suspend fun addAddress(address: AddressCustomer){
        withContext(Dispatchers.IO){
            db.getAddressDao().insertAddress(address)
        }
    }

    suspend fun removeAddress(address: AddressCustomer){
        withContext(Dispatchers.IO){
            db.getAddressDao().deleteCart(address)
        }
    }

    suspend fun changeSelectedAddress(id: Int){
        withContext(Dispatchers.IO){
            db.getAddressDao().unselectAllAddress()
            db.getAddressDao().selectAddress(id)
        }
    }

    suspend fun getEditAddress(id: Int): AddressCustomer{
        return withContext(Dispatchers.IO){
            db.getAddressDao().getEditAddress(id)
        }
    }

    suspend fun updateAddress(address: AddressCustomer){
        withContext(Dispatchers.IO){
            db.getAddressDao().updateAddress(address)
        }
    }

}