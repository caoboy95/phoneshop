package com.example.testapp

import android.app.Application
import com.example.testapp.data.network.NetworkConnectionInterceptor
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class MVVMApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {
        import(androidXModule(this@MVVMApplication))

        bind() from singleton { NetworkConnectionInterceptor(instance()) }
//        bind() from singleton { AuthApi(instance()) }
//        bind() from singleton { AppDatabase(instance()) }
//        bind() from singleton { PreferenceProvider(instance()) }
//        bind() from singleton { UserRepository(instance(), instance()) }
//        bind() from singleton { QuotesRepository(instance(), instance(), instance()) }
//        bind() from provider { AuthViewModelFactory(instance()) }
//        bind() from provider { ProfileViewModelFactory(instance()) }
//        bind() from provider{ QuotesViewModelFactory(instance()) }


    }

}