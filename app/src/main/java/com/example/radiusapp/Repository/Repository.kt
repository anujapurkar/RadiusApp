package com.example.radiusapp.Repository

class Repository constructor(private val apiService: ApiService) {

  suspend fun getFacilities()  = apiService.getFacilitiesDetails()
}