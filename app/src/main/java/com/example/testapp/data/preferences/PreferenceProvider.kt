package net.simplifiedcoding.mvvmsampleapp.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//make data store to save  and retrieve values
//class PreferenceProvider(
//    context: Context
//) {
//    private val applicationContext = context.applicationContext
//    private val dataStore: DataStore<Preferences>
//
//    init {
//        dataStore = applicationContext.createDataStore(
//            name = "my_data_store"
//        )
//    }
//
//    //get back token
//    val authToken: Flow<String?>
//        get() = dataStore.data.map {
//                preferences -> preferences[KEY_AUTH]
//        }
//
//    val lastSaveAt: Flow<String?>
//        get() = dataStore.data.map {
//                preferences -> preferences[KEY_SAVED_AT]
//        }
//
//    suspend fun saveAuthToken(authToken: String){
//        dataStore.edit { preferences->
//            preferences[KEY_AUTH]= authToken
//        }
//    }
//
//    suspend fun savelastSavedAt(savedAt: String){
//        dataStore.edit { preferences->
//            preferences[KEY_SAVED_AT]= savedAt
//        }
//    }
//
//    suspend fun clear(){
//        dataStore.edit { preferences ->
//            preferences.clear()
//        }
//    }
//    //define key
//    companion object{
//        private val KEY_AUTH= stringPreferencesKey("key_auth")
//        private val KEY_SAVED_AT= stringPreferencesKey("key_saved_at")
//    }
//}
private const val KEY_SAVED_AT = "key_saved_at"

class PreferenceProvider(
    context: Context
) {

    private val appContext = context.applicationContext

    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)


    fun savelastSavedAt(savedAt: String) {
        preference.edit().putString(
            KEY_SAVED_AT,
            savedAt
        ).apply()
    }

    fun getLastSavedAt(): String? {
        return preference.getString(KEY_SAVED_AT, null)
    }

}