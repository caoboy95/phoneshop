package com.example.testapp.data.network

import com.example.testapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    companion object{
//        private const val BASE_URL="http://10.0.3.2:8000/api/"
        private const val BASE_URL="http://192.168.1.123:8000/api/"
    }
    fun <Api> buildApi(
        api: Class<Api>,
        networkConnectionInterceptor: NetworkConnectionInterceptor
    ): Api{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .also { client ->
                        if(BuildConfig.DEBUG) {
                            val logging = HttpLoggingInterceptor()
                            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                            client.addInterceptor(networkConnectionInterceptor)
                                .addInterceptor(logging)
                        }
                    }.build()
            ).addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}