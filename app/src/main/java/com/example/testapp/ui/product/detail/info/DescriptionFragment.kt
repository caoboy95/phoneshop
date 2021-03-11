package com.example.testapp.ui.product.detail.info

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.testapp.R
import com.example.testapp.databinding.DescriptionFragmentBinding

class DescriptionFragment : Fragment() {
    private lateinit var viewModel: DescriptionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DescriptionFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.description_fragment, container, false)
        val description = arguments?.getString("description")
        binding.textViewDescription.text= description
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DescriptionViewModel::class.java)
    }
}