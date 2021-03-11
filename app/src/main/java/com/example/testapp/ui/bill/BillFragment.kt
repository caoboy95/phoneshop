package com.example.testapp.ui.bill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.testapp.R
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.repository.BillRepository
import com.example.testapp.databinding.FragmentBillBinding
import com.example.testapp.databinding.FragmentProductBinding
import com.example.testapp.ui.base.BaseFragment


class BillFragment : BaseFragment<BillViewModel, FragmentBillBinding, BillRepository>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCheckBill.setOnClickListener {
            it.findNavController().navigate(BillFragmentDirections.actionBillFragmentToCheckBillFragment(binding.editTextCheckBillPhone.text.toString()))
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBillBinding = FragmentBillBinding.inflate(inflater,container,false)

    override fun getViewModel() = BillViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): BillRepository =
        BillRepository()
}