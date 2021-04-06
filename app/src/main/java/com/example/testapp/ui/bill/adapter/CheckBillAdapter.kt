package com.example.testapp.ui.bill.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.data.db.entities.Bill
import com.example.testapp.data.db.entities.BillAndQuantity
import com.example.testapp.databinding.CheckBillItemAdapterBinding
import com.example.testapp.ui.formatCurrency

class CheckBillAdapter : RecyclerView.Adapter<CheckBillAdapter.CheckBillViewHolder>() {

    private lateinit var clickListener: BillClickListener
    private lateinit var billAndQuantities: List<BillAndQuantity>

    fun setData(billAndQuantities: List<BillAndQuantity>) {
        this.billAndQuantities =  billAndQuantities
        notifyDataSetChanged()
    }

    class CheckBillViewHolder(val binding: CheckBillItemAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(billAndQuantities: BillAndQuantity, clickListener: BillClickListener) {
            binding.textViewCheckBillItemId.text = ("#${billAndQuantities.bill.id}")
            binding.textViewCheckBillItemDate.text = billAndQuantities.bill.date_order
            binding.textViewCheckBillItemTotal.text = formatCurrency(billAndQuantities.bill.total)
            binding.textViewCheckBillItemQuantity.text = billAndQuantities.quantity.toString()

            binding.textViewCheckBillItemStatus.text = when(billAndQuantities.bill.status) {
                0 -> {
                    binding.textViewCheckBillItemStatus.setBackgroundColor(Color.parseColor("#D3A419"))
                    "Chưa Giao Hàng" }
                1 -> {
                    binding.textViewCheckBillItemStatus.setBackgroundColor(Color.parseColor("#D80000"))
                    "Đã Hủy" }
                2 -> {
                    binding.textViewCheckBillItemStatus.setBackgroundColor(Color.parseColor("#06CF0E"))
                    "Đã Giao Hàng" }
                else -> "Không"
            }
            binding.buttonCheckBillDetail.setOnClickListener {
                clickListener.onBillClickListener(billAndQuantities.bill)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckBillViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding : CheckBillItemAdapterBinding = CheckBillItemAdapterBinding.inflate(inflater, parent, false)
        return CheckBillViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckBillViewHolder, position: Int) {
        holder.bind(billAndQuantities[position], clickListener)
    }

    override fun getItemCount() = billAndQuantities.size

    fun setOnBillClickListener(clickListener: BillClickListener) {
        this.clickListener = clickListener
    }

    interface BillClickListener {
        fun onBillClickListener(bill: Bill)
    }

    companion object {
        private const val TAG = "CheckBillAdapter"
    }
}