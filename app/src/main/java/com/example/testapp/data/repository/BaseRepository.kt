package com.example.testapp.data.repository

import com.example.fullmvvm.util.NoInternetException
import com.example.testapp.Constant
import com.example.testapp.data.network.Resource
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

abstract class BaseRepository {
    val firebaseDatabase = FirebaseDatabase.getInstance()
    val firebaseStorage = FirebaseStorage.getInstance()

    fun getProductImageFromFirebase(url: String) =
            firebaseStorage.getReferenceFromUrl(Constant.URL_FIREBASE_STORAGE).child("product/$url").downloadUrl

    suspend fun <T> safeApiCall(
            apiCall: suspend () -> T
    ): Resource<T> {
        return withContext(Dispatchers.IO){
            try{
                Resource.Success(apiCall.invoke())
            }
            catch (throwable: Throwable){
                when(throwable){
                    is HttpException -> {
                        Resource.Failure(true,throwable.code(),throwable.response()?.errorBody(),null)
                    }
                    is NoInternetException -> {
                        Resource.Failure(false,null,null,"Make sure you have an active data connection")
                    }
                    else -> {
                        Resource.Failure(false,null,null,throwable.message)
                    }
                }
            }

        }
    }
    //call api with parameter
//    suspend fun <T> safeApiCall(
//        id:Int,
//        apiCall: suspend (Int) ->T
//    ): Resource<T> {
//        return withContext(Dispatchers.IO){
//            try{
//                Resource.Success(apiCall.invoke(id))
//            }
//            catch (throwable: Throwable){
//                when(throwable){
//                    is HttpException -> {
//                        Resource.Failure(true,throwable.code(),throwable.response()?.errorBody(),null)
//                    }
//                    is NoInternetException -> {
//                        Resource.Failure(false,null,null,throwable.message)
//                    }
//                    else -> {
//                        Resource.Failure(false,null,null,throwable.message)
//                    }
//                }
//            }
//        }
//    }


}




