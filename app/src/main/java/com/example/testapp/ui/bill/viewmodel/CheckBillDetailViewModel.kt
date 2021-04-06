package com.example.testapp.ui.bill.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.testapp.data.db.entities.*
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.CheckBillDetailRepository
import com.example.testapp.data.response.BillDetailResponse
import com.example.testapp.ui.base.BaseViewModel
import com.example.testapp.ui.getDataValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class CheckBillDetailViewModel(
    private val repository: CheckBillDetailRepository
) : BaseViewModel(repository) {

    private val _billDetailsFB: MutableLiveData<BillDetailResponse> = MutableLiveData()
    val billDetailsFB : LiveData<BillDetailResponse>
        get() = _billDetailsFB

    private val _billDetails: MutableLiveData<Resource<BillDetailResponse>> = MutableLiveData()
    val billDetail : LiveData<Resource<BillDetailResponse>>
        get() = _billDetails

    lateinit var bill: Bill

    fun setData(bill : Bill) {
        this.bill = bill
//        getBillDetails(this.bill.id)
        getBillDetailsFromFirebase(this.bill.id)
    }

    private fun getBillDetails(idBill: Int) {
        viewModelScope.launch {
            _billDetails.value = Resource.Loading
            _billDetails.value = repository.getBillDetails(idBill)
        }
    }

    private fun getBillDetailsFromFirebase(idBill: Int) {
        repository.getBillDetailsFromFirebase(idBill).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val billDetails = snapshot.getDataValue(BillDetail::class.java)
                val billDetailsInfo = mutableListOf<BillDetailsInfo>()
                billDetails.forEach { billDetail ->
                    repository.getProductVariantOfBill(billDetail.id_product_variant).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val productVariants = snapshot.getDataValue(ProductVariant::class.java)

                            repository.getProductFromFirebase(productVariants.first().id_product).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val products = snapshot.getDataValue(Product::class.java)

                                    repository.getImageOfVariant(productVariants.first().id_image).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val images = snapshot.getDataValue(Image::class.java)
                                            billDetailsInfo.add(BillDetailsInfo(images.first(), products.first().name,
                                                    billDetail.unit_price, productVariants.first(), billDetail.quantity))
                                            if (billDetail == billDetails.last()) {
                                                _billDetailsFB.value = BillDetailResponse(billDetailsInfo)
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e(TAG, "Error Image: $error")
                                        }
                                    })
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e(TAG, "Error Product: $error")
                                }
                            })
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "Error ProductVariant: $error")
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error BillDetail: $error")
            }
        })
    }

    fun getRepository() = repository

    companion object {
        private const val TAG = "CheckBillDetailVM"
    }
}