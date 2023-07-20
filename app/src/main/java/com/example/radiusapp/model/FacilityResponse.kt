package com.example.radiusapp.model

data class FacilityResponse(
    val exclusions: ArrayList<ArrayList<Exclusion>>,
    val facilities: ArrayList<Facility>
)