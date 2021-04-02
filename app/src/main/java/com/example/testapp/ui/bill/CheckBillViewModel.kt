package com.example.testapp.ui.bill

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp.data.db.entities.*
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.CheckBillRepository
import com.example.testapp.data.response.BillResponse
import com.example.testapp.ui.base.BaseViewModel
import com.example.testapp.ui.getDataValue
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch
import java.util.Arrays.sort
import java.util.Collections.sort

class CheckBillViewModel(
    val repository: CheckBillRepository
) : BaseViewModel(repository) {

    private val _billsFB : MutableLiveData<BillsWithCustomer> = MutableLiveData()
    val billsFB: LiveData<BillsWithCustomer>
        get() = _billsFB

    private val _bills : MutableLiveData<Resource<BillResponse>> = MutableLiveData()
    val bills: LiveData<Resource<BillResponse>>
        get() = _bills
    private lateinit var phone: String
    private lateinit var notifyCustomerExist: NotifyCustomerExist

    fun setData(phone: String) {
        this.phone=phone
//        getBills()
        getBillsFromFirebase()
    }

    private fun getBills() {
        viewModelScope.launch {
            _bills.value = Resource.Loading
            _bills.value = repository.getBills(phone)
        }
    }

    private fun getBillsFromFirebase() {
        repository.getCustomerFromFirebase(phone).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshotCustomer: DataSnapshot) {
                val customers = snapshotCustomer.getDataValue(Customer::class.java)
                if (customers.isNotEmpty()) {
                    repository.getBillsFromFirebase(customers[0].id).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshotBill: DataSnapshot) {
                            val bills = snapshotBill.getDataValue(Bill::class.java)
                            var billAndQuantities = mutableListOf<BillAndQuantity>()

                            bills.forEach { bill ->
                                repository.getBillDetailsFromFirebase(bill.id).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshotBillDetail: DataSnapshot) {
                                        val billDetails = snapshotBillDetail.getDataValue(BillDetail::class.java)
                                        val quantity = billDetails.map { it.quantity }.sum()
                                        billAndQuantities.add(BillAndQuantity(bill, quantity))
                                        if (bills.last() == bill) {
                                            _billsFB.value = BillsWithCustomer(billAndQuantities, customers[0])
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Log.e(TAG, "Error: $error")
                                    }
                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e(TAG, "Error: $error")
                        }

                    })
                    return
                }
                notifyCustomerExist.notifyHaveNoCustomer()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error: $error")
            }

        })
    }

    fun setNotifyInterface(notifyCustomerExist: NotifyCustomerExist) {
        this.notifyCustomerExist = notifyCustomerExist
    }

    interface NotifyCustomerExist {
        fun notifyHaveNoCustomer()
    }

    companion object {
        private const val TAG = "CheckBillViewModel"
    }
}