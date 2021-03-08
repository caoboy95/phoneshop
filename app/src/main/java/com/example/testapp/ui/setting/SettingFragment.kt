package com.example.testapp.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.testapp.R
import com.example.testapp.databinding.FragmentProductBinding
import com.example.testapp.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding : FragmentSettingBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_setting, container, false)
        return binding.root
    }


}