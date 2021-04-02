package com.example.testapp.ui.bill

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavArgs
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fullmvvm.util.Coroutines
import com.example.testapp.R
import com.example.testapp.data.db.entities.Bill
import com.example.testapp.data.db.entities.BillAndQuantity
import com.example.testapp.data.db.entities.BillsWithCustomer
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.network.Resource
import com.example.testapp.data.repository.CheckBillRepository
import com.example.testapp.databinding.CheckBillFragmentBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.handleApiError
import com.example.testapp.ui.snackbar
import com.example.testapp.ui.visible

class CheckBillFragment : BaseFragment<CheckBillViewModel, CheckBillFragmentBinding, CheckBillRepository>() {

    val safeArgs : CheckBillFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setNotifyInterface(object : CheckBillViewModel.NotifyCustomerExist {
            override fun notifyHaveNoCustomer() {
                this@CheckBillFragment.view?.snackbar(resources.getString(R.string.notify_customer_exist))
                this@CheckBillFragment.findNavController().navigateUp()
            }
        })
        viewModel.setData(safeArgs.phone)
        viewModel.billsFB.observe(viewLifecycleOwner, Observer {
            it?.let {
                updateUI(it)
                binding.progressBar.visible(false)
            }
        })
    }

    private fun updateUI(billsWithCustomer: BillsWithCustomer) {
        binding.customer = billsWithCustomer.customer
        initRecyclerView(billsWithCustomer.billAndQuantity)
    }

    private fun initRecyclerView(billAndQuantity: List<BillAndQuantity>) {
        val mAdapter = CheckBillAdapter()
        mAdapter.setOnBillClickListener(object : CheckBillAdapter.BillClickListener {
            override fun onBillClickListener(bill: Bill) {
                this@CheckBillFragment.view?.findNavController()?.navigate(CheckBillFragmentDirections.actionCheckBillFragmentToCheckBillDetailFragment(bill))
            }
        })
        mAdapter.setData(billAndQuantity)
        binding.recyclerViewCheckBillItem.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            setHasFixedSize(true)
            adapter= mAdapter
        }
    }

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): CheckBillFragmentBinding = CheckBillFragmentBinding.inflate(inflater,container,false)

    override fun getViewModel() = CheckBillViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor): CheckBillRepository =
        CheckBillRepository(remoteDataSource.buildApi(ProductApi::class.java,networkConnectionInterceptor))
}