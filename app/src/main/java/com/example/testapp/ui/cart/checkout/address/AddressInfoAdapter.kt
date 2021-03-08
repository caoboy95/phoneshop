package com.example.testapp.ui.cart.checkout.address

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp.data.db.entities.AddressCustomer
import com.example.testapp.databinding.AddressInfoAdapterBinding

class AddressInfoAdapter(): RecyclerView.Adapter<AddressInfoAdapter.AddressViewHolder>() {


    lateinit var clickListener: AddressClickListener
    private lateinit var addresses: List<AddressCustomer>

    fun setData(addresses: List<AddressCustomer>){
        this.addresses = addresses
        notifyDataSetChanged()
    }

    class AddressViewHolder(val binding : AddressInfoAdapterBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(address: AddressCustomer){
            binding.layoutAddressInfo.address=address
            binding.radioButtonSelect.isChecked= address.selected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AddressInfoAdapterBinding.inflate(inflater,parent,false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(addresses[position])
        holder.binding.buttonDeleteAddress.setOnClickListener {
            clickListener.onRemoveClickListener(addresses[position])
        }
        holder.binding.buttonEditAddress.setOnClickListener {
            clickListener.onEditClickListener(addresses[position].id)
        }
//        holder.binding
        holder.binding.rootLayout.setOnClickListener {
            if(!addresses[position].selected)
                clickListener.onSelectedClickListener(addresses[position].id)
        }
    }

    override fun getItemCount(): Int = addresses.size

    fun setOnClickListener(clickListener: AddressClickListener){
        this.clickListener=clickListener
    }

    interface AddressClickListener{
        fun onEditClickListener(id: Int)
        fun onRemoveClickListener(addressCustomer: AddressCustomer)
        fun onSelectedClickListener(id:Int)
    }
}