package com.example.testapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.testapp.data.db.entities.*

@Database(
    entities = [Product::class,Cart::class, AddressCustomer::class],
    version = 2, exportSchema = false
)
@TypeConverters(CartItemsConverters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getProductDao(): ProductDao
    abstract fun getCartDao(): CartDao
    abstract fun getAddressDao(): AddressDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private var LOCK= Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "ShopDatabase.db"
            ).build()
    }
}