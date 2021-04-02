package com.example.testapp.ui.cart.checkout

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapp.R
import com.example.testapp.data.db.entities.*
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.ProductApi
import com.example.testapp.data.repository.CartRepository
import com.example.testapp.databinding.AlertCheckoutLayoutBinding
import com.example.testapp.databinding.CheckOutFragmentBinding
import com.example.testapp.ui.base.BaseFragment
import com.example.testapp.ui.formatCurrency
import com.example.testapp.ui.getDataValue
import com.example.testapp.ui.snackbar
import com.example.testapp.ui.visible
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class CheckOutFragment : BaseFragment<CheckOutViewModel, CheckOutFragmentBinding, CartRepository>() {
    var address : AddressCustomer? = null
    var alertBuilder: AlertDialog.Builder? = null
    private val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("us")).format(Timestamp(System.currentTimeMillis()))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSelectedAddress()
        viewModel.cart.observe(viewLifecycleOwner, Observer CartObserve@{ cart ->
            viewModel.address.observe(viewLifecycleOwner, Observer AddressObserve@{ address ->
                this.address = address
                address?.let {
                    updateAddressUI(it)
                    return@AddressObserve
                }
                binding.addressInfo.visible(false)
                binding.textViewRequiredAddress.visible(true)
                this.view?.snackbar(NOTIFY_REQUIRED_ADDRESS)
            })
            cart?.let {
                updateUI(it)
                return@CartObserve
            }
            refreshUI()
        })
    }

    private fun refreshUI() {
        binding.recyclerViewCartItemInfo.visible(false)
        binding.textViewTotalPrice.text = formatCurrency(0)
        binding.buttonOrder.setOnClickListener {
            it.snackbar(NOTIFY_CART_NOTHING)
        }
    }

    private fun updateAddressUI(address: AddressCustomer){
        binding.addressInfo.visible(true)
        binding.textViewRequiredAddress.visible(false)
        binding.layoutAddressInfo.address = address
    }

    private fun setAlertDialog(cart: Cart, address: AddressCustomer){
        alertBuilder = activity?.let {
            AlertDialog.Builder(it)
        }
        val inflater = requireActivity().layoutInflater
        val alertBinding: AlertCheckoutLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.alert_checkout_layout, requireView().parent as ViewGroup,false)
        alertBinding.textViewTotal.text = formatCurrency(cart.totalPrice)
        alertBinding.textViewName.text = address.name
        alertBinding.textViewAddress.text = address.address
        alertBinding.textViewEmail.text = address.email
        alertBinding.textViewPhone.text = address.phone
        alertBinding.textViewGender.text = address.gender
        alertBinding.textViewQuantity.text = cart.totalQty.toString()
        alertBuilder?.apply {
            setView(alertBinding.root)
            setPositiveButton("OK") { _, _ ->
                binding.progressBar.visible(true)
                checkOut(cart, address)
                //Rest API
//                lifecycleScope.launch {
//                    viewModel.checkOutAsync(CartAndAddress(cart, address)).await().also {
//                        when (it) {
//                            is Resource.Success -> {
//                                if (it.value.isSuccessful) {
//                                    this@CheckOutFragment.view?.snackbar(it.value.checkOutMessage)
//                                    viewModel.removeCart(cart)
//                                    val action = CheckOutFragmentDirections.actionCheckOutFragmentToCheckOutNotifyFragment(it.value.idBill, CartAndAddress(cart, address))
//                                    this@CheckOutFragment.findNavController().navigate(action)
//                                } else {
//                                    this@CheckOutFragment.view?.snackbar(it.value.checkOutMessage)
//                                    this@CheckOutFragment.view?.findNavController()?.navigateUp()
//                                }
//                            }
//                            is Resource.Failure -> this@CheckOutFragment.view?.snackbar(it.message.toString())
//                        }
//                    }
//                }

            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            setTitle(NOTIFY_CHECK_INFO)
            create()
            show()
        }
    }

    private fun updateUI(cart: Cart) {
        cart.items?.let {
            initRecyclerView(it.cartItems as List<CartItem>)
        }
        binding.textViewTotalPrice.text = formatCurrency(cart.totalPrice)
        binding.buttonEditShippingInfo.setOnClickListener {
            it.findNavController().navigate(CheckOutFragmentDirections.actionCheckOutFragmentToAddressFragment())
        }
        binding.buttonOrder.setOnClickListener { view ->
            address?.let {
                setAlertDialog(cart, it)
                return@setOnClickListener
            }
            view.snackbar(NOTIFY_REQUIRED_ADDRESS)
        }
    }

    private fun initRecyclerView(cart: List<CartItem>){
        val mAdapter = CartItemAdapter(viewModel.getRepository())
        mAdapter.setData(cart)
        binding.recyclerViewCartItemInfo.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }
    //Checkout for Firebase database
    private fun checkOut(cart: Cart, address: AddressCustomer) {
        viewModel.getCustomers().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshotCustomer: DataSnapshot) {
                val customers = snapshotCustomer.getDataValue(Customer::class.java)
                val customer = createCustomer(customers, address)

                viewModel.getBills().addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshotBill: DataSnapshot) {
                        val bills = snapshotBill.getDataValue(Bill::class.java)
                        val bill = Bill(time, time, checkBillID(bills, bills.size), customer.id, 0, 0,
                                "", "COD", 0, cart.totalPrice, time)

                        viewModel.getProductVariants().addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshotProductVariant: DataSnapshot) {
                                if (snapshotProductVariant.exists()) {
                                    val productVariants = snapshotProductVariant.getDataValue(ProductVariant::class.java)
                                    cart.items?.cartItems?.forEach { cartItem ->
                                        productVariants.find { it.id == cartItem.item.id }?.let {
                                            if ((it.quantity - cartItem.quantity) < 0) {
                                                viewModel.getProduct(it.id_product).addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        val products = snapshot.getDataValue(Product::class.java)
                                                        this@CheckOutFragment.view?.snackbar(notifyOutOfStock("${products[0].name} ${it.version} ${it.color}"))
                                                        binding.progressBar.visible(false)
                                                        this@CheckOutFragment.view?.findNavController()?.navigateUp()
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        this@CheckOutFragment.view?.snackbar(NOTIFY_ERROR)
                                                        binding.progressBar.visible(false)
                                                    }
                                                })
                                                return //skip below code if out of stock
                                            }
                                        }
                                    }
                                    viewModel.getBillDetails().addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshotBillDetail: DataSnapshot) {
                                            if (snapshotBillDetail.exists()) {
                                                if (!customers.contains(customer)) {
                                                    snapshotCustomer.ref.child(customer.id.toString()).setValue(customer)
                                                }
                                                snapshotBill.ref.child(bill.id.toString()).setValue(bill)
                                                snapshotProductVariant.children.forEach { dataSnapshot ->
                                                    dataSnapshot.getValue(ProductVariant::class.java)?.let { productVariant ->
                                                        cart.items?.cartItems?.find { it.item.id == productVariant.id }?.let {
                                                            snapshotProductVariant.ref.child(dataSnapshot.key.toString()).child("quantity").setValue(productVariant.quantity.minus(it.quantity))
                                                        }
                                                    }
                                                }
                                                val billDetails = snapshotBillDetail.getDataValue(BillDetail::class.java)
                                                var billDetailID = checkBillDetailID(billDetails, billDetails.size)
                                                cart.items?.cartItems?.forEach { cartItem ->
                                                    val billDetail = BillDetail(time, billDetailID, bill.id, cartItem.item.id,
                                                            cartItem.quantity, cartItem.price, time)
                                                    snapshotBillDetail.ref.child(billDetail.id.toString()).setValue(billDetail)
                                                    billDetailID++
                                                }
                                                this@CheckOutFragment.view?.snackbar(NOTIFY_SUCCESS)
                                                binding.progressBar.visible(false)
                                                viewModel.removeCart(cart)
                                                val action = CheckOutFragmentDirections.actionCheckOutFragmentToCheckOutNotifyFragment(bill.id, CartAndAddress(cart, address))
                                                this@CheckOutFragment.findNavController().navigate(action)
                                                return
                                            }
                                            this@CheckOutFragment.view?.snackbar(NOTIFY_ERROR)
                                            binding.progressBar.visible(false)
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            this@CheckOutFragment.view?.snackbar(NOTIFY_ERROR)
                                            Log.e(TAG, "Error: $error")
                                            binding.progressBar.visible(false)
                                        }

                                    })
                                    return
                                }
                                this@CheckOutFragment.view?.snackbar(NOTIFY_ERROR)
                                binding.progressBar.visible(false)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                this@CheckOutFragment.view?.snackbar(NOTIFY_ERROR)
                                Log.e(TAG, "Error: $error")
                                binding.progressBar.visible(false)
                            }

                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        this@CheckOutFragment.view?.snackbar(NOTIFY_ERROR)
                        Log.e(TAG, "Error: $error")
                        binding.progressBar.visible(false)
                    }

                })
            }

            override fun onCancelled(error: DatabaseError) {
                this@CheckOutFragment.view?.snackbar(NOTIFY_ERROR)
                Log.e(TAG, "Error: $error")
                binding.progressBar.visible(false)
            }
        })
    }

    fun createCustomer(customers: List<Customer>, address: AddressCustomer) : Customer {
        customers.find { it.phone_number == address.phone }?.let {
            return it
        }
        customers.map { it.id }.maxOrNull()?.let {
            val customerID = checkCustomerID(customers, it+1)
            return Customer(address.address, time, address.email, address.gender,
                    customerID, 0, address.name, "", address.phone, time)
        }
        return Customer(address.address, time, address.email, address.gender,
                checkCustomerID(customers, customers.size), 0, address.name, "", address.phone, time)
    }

    private fun checkCustomerID(customers: List<Customer>, customerID: Int) : Int {
        var id = customerID
        customers.find { it.id == customerID }?.let {
            return checkCustomerID(customers, ++id)
        }
        return id
    }

    private fun checkBillID(bills: List<Bill>, billID: Int) : Int {
        var id = billID
        bills.find { it.id == billID }?.let {
            return checkBillID(bills, ++id)
        }
        return id
    }

    private fun checkBillDetailID(billDetails: List<BillDetail>, billDetailID: Int) : Int {
        var id = billDetailID
        billDetails.find { it.id == billDetailID }?.let {
            return checkBillDetailID(billDetails, ++id)
        }
        return id
    }

    override fun getFragmentBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): CheckOutFragmentBinding = CheckOutFragmentBinding.inflate(inflater, container, false)

    override fun getViewModel() = CheckOutViewModel::class.java

    override fun getFragmentRepository(networkConnectionInterceptor: NetworkConnectionInterceptor) =
            CartRepository(db, remoteDataSource.buildApi(ProductApi::class.java, networkConnectionInterceptor))

    companion object {
        private const val TAG = "CheckOutFragment"
        private const val NOTIFY_REQUIRED_ADDRESS = "Vui Lòng Nhập Địa Chỉ!"
        private const val NOTIFY_CHECK_INFO = "Hãy kiểm tra thông tin trước khi đặt hàng!"
        private const val NOTIFY_CART_NOTHING = "Giỏ hàng không có sản phẩm!"
        private const val NOTIFY_ERROR = "Lỗi kết nối server!"
        private const val NOTIFY_SUCCESS = "Đặt hàng thành công"
        private fun notifyOutOfStock(name: String) = "Sản phẩm $name đã hết hàng!"

    }
}