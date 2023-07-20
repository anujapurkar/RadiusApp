package com.example.radiusapp.Repository

import com.example.radiusapp.model.FacilityResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("ad-assignment/db")
  suspend  fun getFacilitiesDetails(): Response<FacilityResponse?>

    companion object{
        var apiService : ApiService? = null

        fun  getInstance(): ApiService{
            if (apiService ==null){
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://my-json-server.typicode.com/iranjith4/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                apiService = retrofit.create(ApiService::class.java)
            }
            return apiService as ApiService
        }
    }
}