package com.example.radiusapp.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.radiusapp.Repository.Repository
import com.example.radiusapp.model.Facility
import com.example.radiusapp.model.FacilityResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel constructor( private val repository: Repository) : ViewModel() {

    private val _facilitiesLiveData = MutableLiveData<FacilityResponse>()
    val facilitiesLiveData = _facilitiesLiveData

    val errorMessage = MutableLiveData<String>()

    fun getFacilities(){
        CoroutineScope(Dispatchers.IO).launch {
            val facilitiesResponse = repository.getFacilities()
            Log.d("FacilityData++", "getFacilities: "+facilitiesResponse.toString())
            withContext(Dispatchers.Main){
               if (facilitiesResponse.isSuccessful) {
                    _facilitiesLiveData.postValue(facilitiesResponse.body())
                }else{
                    onError("Error Message ${facilitiesResponse.message()}")
               }

            }
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
    }

}