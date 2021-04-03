package com.example.testapp.ui.bill

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.testapp.R
import com.example.testapp.data.db.entities.Customer
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.repository.BillRepository
import com.example.testapp.databinding.FragmentBillBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.getDataValue
import com.example.testapp.ui.snackbar
import com.example.testapp.ui.visible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class BillFragment : BaseFragment<BillViewModel, FragmentBillBinding, BillRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.bringToFront()
        binding.buttonCheckBill.setOnClickListener {
            it?.let {
                hideKeyboard(it)
            }
            binding.progressBar.visible(true)
            viewModel.getCustomer(binding.editTextCheckBillPhone.text.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val customers = snapshot.getDataValue(Customer::class.java)
                    if (customers.isNotEmpty()) {
                        it.findNavController().navigate(
                            BillFragmentDirections.actionBillFragmentToCheckBillFragment(
                                binding.editTextCheckBillPhone.text.toString()
                            )
                        )
                        binding.progressBar.visible(false)
                        return
                    }
                    this@BillFragment.view?.snackbar(resources.getString(R.string.notify_customer_exist))
                    binding.progressBar.visible(false)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error: $error")
                }
            })
        }
        binding.editTextCheckBillPhone.setOnFocusChangeListener(object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!hasFocus) {
                    v?.let {
                        hideKeyboard(v)
                    }
                }
            }
        })
        requireActivity().actionBar?.apply {
            setHomeButtonEnabled(false)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowHomeEnabled(false)
        }
    }

    fun hideKeyboard(view: View) {
        val inputMethodManager =
            ContextCompat.getSystemService(this.requireContext(), InputMethodManager::class.java)
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBillBinding = FragmentBillBinding.inflate(inflater, container, false)

    override fun getViewModel() = BillViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): BillRepository =
        BillRepository()

    companion object {
        private const val TAG = "BillFragment"
    }
}