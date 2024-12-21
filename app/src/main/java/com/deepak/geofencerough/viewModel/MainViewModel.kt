package com.deepak.geofencerough.viewModel

import androidx.collection.emptyObjectList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deepak.geofencerough.GeofenceApplication
import com.deepak.geofencerough.model.LocationDetails
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val realm = GeofenceApplication.realm

    fun addLocation(locationDetails: LocationDetails) {
        viewModelScope.launch {
            realm.write {
                copyToRealm(locationDetails, updatePolicy = UpdatePolicy.ALL)
            }
        }
    }

    fun queryLocationDetails(): List<LocationDetails> {

        return realm.query(LocationDetails::class).find()
    }
}