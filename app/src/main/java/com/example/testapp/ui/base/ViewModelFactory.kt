package com.example.testapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.data.repository.*
import com.example.testapp.ui.bill.BillViewModel
import com.example.testapp.ui.bill.CheckBillViewModel
import com.example.testapp.ui.bill.detail.CheckBillDetailViewModel
import com.example.testapp.ui.cart.CartViewModel
import com.example.testapp.ui.cart.checkout.CheckOutViewModel
import com.example.testapp.ui.cart.checkout.address.AddressViewModel
import com.example.testapp.ui.product.viewmodel.ProductViewModel
import com.example.testapp.ui.product.viewmodel.ProductDetailViewModel
import com.example.testapp.ui.product.viewmodel.InfoViewModel

class ViewModelFactory(
    private val repository: BaseRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(ProductViewModel::class.java) -> ProductViewModel(repository as ProductRepository) as T
            modelClass.isAssignableFrom(ProductDetailViewModel::class.java) -> ProductDetailViewModel(repository as ProductDetailRepository) as T
            modelClass.isAssignableFrom(CartViewModel::class.java) -> CartViewModel(repository as CartRepository) as T
            modelClass.isAssignableFrom(InfoViewModel::class.java) -> InfoViewModel(repository as ProductInfoRepository) as T
            modelClass.isAssignableFrom(CheckOutViewModel::class.java) -> CheckOutViewModel(repository as CartRepository) as T
            modelClass.isAssignableFrom(AddressViewModel::class.java) -> AddressViewModel(repository as AddressRepository) as T
            modelClass.isAssignableFrom(BillViewModel::class.java) -> BillViewModel(repository as BillRepository) as T
            modelClass.isAssignableFrom(CheckBillViewModel::class.java) -> CheckBillViewModel(repository as CheckBillRepository) as T
            modelClass.isAssignableFrom(CheckBillDetailViewModel::class.java) -> CheckBillDetailViewModel(repository as CheckBillDetailRepository) as T
            else -> throw IllegalArgumentException("ViewModelClass Not Found")
        }
    }
}