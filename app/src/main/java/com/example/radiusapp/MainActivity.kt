package com.example.radiusapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.radiusapp.Repository.ApiService
import com.example.radiusapp.Repository.Repository
import com.example.radiusapp.databinding.ActivityMainBinding
import com.example.radiusapp.model.Exclusion

import com.example.radiusapp.model.Facility
import com.example.radiusapp.model.GridItemInfo
import com.example.radiusapp.ui.MainViewModel
import com.example.radiusapp.ui.MainViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    lateinit var binding: ActivityMainBinding
    private var facilityList = ArrayList<Facility>()
    private var exclusionList = ArrayList<ArrayList<Exclusion>>()
    private var childExclusionList = ArrayList<Exclusion>()
    private var idToViewMap = mutableMapOf<String, View>()

    private val TAG= MainActivity::class.java


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val facilityInstance = ApiService.getInstance()
        val facilityRepository = Repository(facilityInstance)

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(facilityRepository)
        )[MainViewModel::class.java]

        viewModel.getFacilities()

        updateRadiusUI()
    }

    private fun updateRadiusUI() {
        viewModel.facilitiesLiveData.observe(this) {
            facilityList = it.facilities
            exclusionList = it.exclusions
            //  Log.d("FacilityData++", "updateRadiusUI: Succcess"+exclusionList)
            facilityList.forEach {
                addNewView(it)
            }
        }
        viewModel.errorMessage.observe(this) {
            //  Log.d("FacilityData++", "updateRadiusUI: Error"+it)
        }
        updateBgnColors()
    }

    private fun addNewView(facility: Facility) {
        // this method inflates the single item layout
        // inside the parent linear layout
        // Log.d("FacilityData++", "updateRadiusUI: Succcess$facility")
        val rootView = LayoutInflater.from(this).inflate(R.layout.row_item_layout, null)
        val headerText = rootView.findViewById<TextView>(R.id.HeaderText)
        headerText.text = facility.name

        val itemLayout = rootView.findViewById<LinearLayout>(R.id.itemLayout)
        facility.options.forEach {
            val childView = LayoutInflater.from(this).inflate(R.layout.dynamic_layout, null)
            idToViewMap.put(it.id, childView)
            childView.setTag(GridItemInfo(facility.facility_id, it.id))
            val itemText = childView.findViewById<TextView>(R.id.textView)
            val propertyImage = childView.findViewById<ImageView>(R.id.imageView)
            itemText.text = it.name

            childView.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected) {
                    Toast.makeText(this, "${itemText.text} selected", Toast.LENGTH_SHORT).show()
                    childView.setBackgroundColor(resources.getColor(androidx.appcompat.R.color.primary_material_dark))
                } else {
                    childView.setBackgroundColor(resources.getColor(R.color.transparent))
                }
                deselectForSameFacility(it.getTag() as GridItemInfo)
                deselctForHigherFacilityId(it.getTag() as GridItemInfo)
                updateViewsForExclusions(it.getTag() as GridItemInfo)
                updateBgnColors()
            }
            when (it.icon) {
                "apartment" -> propertyImage.setImageResource(R.drawable.apartment)
                "condo" -> propertyImage.setImageResource(R.drawable.condo)
                "boat" -> propertyImage.setImageResource(R.drawable.boat)
                "land" -> propertyImage.setImageResource(R.drawable.land)
                "rooms" -> propertyImage.setImageResource(R.drawable.rooms)
                "no-room" -> propertyImage.setImageResource(R.drawable.no_room)
                "swimming" -> propertyImage.setImageResource(R.drawable.swimming)
                "garden" -> propertyImage.setImageResource(R.drawable.garden)
                "garage" -> propertyImage.setImageResource(R.drawable.garage)
            }



            itemLayout.addView(childView)
        }
        binding.propertyMainLayout.addView(rootView)

    }

    private fun deselctForHigherFacilityId(selectedGridItemInfo: GridItemInfo) {
        idToViewMap.forEach {
            if ((it.value.getTag() as GridItemInfo).facilityId > selectedGridItemInfo.facilityId) {
                it.value.isSelected = false
                it.value.isEnabled = true
            }
        }
    }

    private fun deselectForSameFacility(gridItemInfo: GridItemInfo) {
        idToViewMap.forEach {
            if ((it.value.getTag() as GridItemInfo).facilityId == gridItemInfo.facilityId) {
                if ((it.value.getTag() as GridItemInfo).optionId != gridItemInfo.optionId) {
                    it.value.isSelected = false
                    it.value.isEnabled = true
                }
            }
        }
    }

    private fun updateViewsForExclusions(selectedGridItemInfo: GridItemInfo) {
        idToViewMap.forEach {
            if ((it.value.getTag() as GridItemInfo).facilityId > selectedGridItemInfo.facilityId) {
                it.value.isEnabled = true
            }
        }
        exclusionList.forEach {
            var matchIndex = -1
            for (i in 0..it.size - 1) {
                if (selectedGridItemInfo.optionId.equals(it.get(i).options_id)) {
                    matchIndex = i
                    break
                }
            }
            if (matchIndex >= 0) {
                for (i in 0..it.size - 1) {
                    if (i != matchIndex && (it.get(i).facility_id > selectedGridItemInfo.facilityId)) {
                        idToViewMap.get(it.get(i).options_id)?.isEnabled = false
                    }
                }
            }
        }

    }

    private fun updateBgnColors() {
        idToViewMap.forEach {
            if (it.value.isEnabled) {
                if (it.value.isSelected) {
                    it.value.setBackgroundColor(Color.GREEN)
                } else {
                    it.value.setBackgroundColor(Color.TRANSPARENT)
                }
            } else {
                it.value.setBackgroundColor(Color.LTGRAY)
            }
        }
    }

    private fun excludeSelection(facilityId: String, optionId: String) {
        exclusionList.forEach {
            childExclusionList = it
            Log.d(
                "FacilityData++",
                "addNewView: Details" + it[0].options_id + " = " + it[1].options_id
            )
            Log.d("FacilityData++", "addNewView: selected" + facilityId + " = " + optionId)
        }
    }

    fun onClearClicked(view: View) {
        idToViewMap.forEach {
            it.value.isSelected = false
            it.value.isEnabled = true
            updateBgnColors()
        }
    }





}


