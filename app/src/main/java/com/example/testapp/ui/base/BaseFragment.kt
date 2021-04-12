package com.example.testapp.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.testapp.data.db.AppDatabase
import com.example.testapp.data.network.NetworkConnectionInterceptor
import com.example.testapp.data.network.RemoteDataSource
import com.example.testapp.data.repository.BaseRepository
import com.jakewharton.threetenabp.AndroidThreeTen
import net.simplifiedcoding.mvvmsampleapp.data.preferences.PreferenceProvider

abstract class BaseFragment<VM:BaseViewModel, B: ViewDataBinding, R:BaseRepository> : Fragment() {
    //instance of viewmodel
    protected lateinit var viewModel: VM
    //instance of
    protected  lateinit var binding : B
    //instance of remotedatasource
    protected val remoteDataSource = RemoteDataSource()

    protected lateinit var prefs: PreferenceProvider

    protected lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        appDatabase = AppDatabase.invoke(requireContext())
        prefs = PreferenceProvider(requireContext())
        binding = getFragmentBinding(inflater, container)
        val factory = ViewModelFactory(getFragmentRepository(NetworkConnectionInterceptor(requireContext())))
        viewModel = ViewModelProvider(this, factory).get(getViewModel())
        AndroidThreeTen.init(requireContext())
        return binding.root
    }

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): B

    abstract fun getViewModel(): Class<VM>

    abstract fun getFragmentRepository(networkConnectionInterceptor : NetworkConnectionInterceptor): R
}