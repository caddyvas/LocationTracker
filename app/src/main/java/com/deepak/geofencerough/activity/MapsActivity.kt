package com.deepak.geofencerough.activity

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.RelativeLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.deepak.geofencerough.GeoFenceHelper
import com.deepak.geofencerough.R
import com.deepak.geofencerough.databinding.ActivityMapsBinding
import com.deepak.geofencerough.getMyShoppingListTheme
import com.deepak.geofencerough.model.LocationDetails
import com.deepak.geofencerough.viewModel.MainViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.Locale


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    // Geofencing
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeoFenceHelper
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //private var geofenceHelper : GeoFenceHelper
    private var FINE_LOCATION_ACCESS_REQUEST_CODE = 10001
    private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002
    private var GEOFENCE_ID = "GEOFENCE_ID"
    private var GEOFENCE_RADIUS: Float = 100F
    private val TAG = "MapsActivityU"

    private lateinit var mapFragment: SupportMapFragment

    private lateinit var mapView: View

    // map search view
    private lateinit var searchView: SearchView

    private lateinit var geocoder: Geocoder

    private lateinit var composeView: ComposeView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // alert Dialog using composable
        composeView = findViewById(R.id.compose_view)

        geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        searchView = findViewById(R.id.mapSearch)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val location: String = searchView.query.toString()
                var addressList: List<android.location.Address>? = null
                if (location != null) {
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    val address: android.location.Address? = addressList?.get(0)
                    val latLng: LatLng? = address?.latitude?.let {
                        LatLng(
                            it,
                            address.longitude
                        )
                    }
                    latLng?.let { MarkerOptions().position(it).title("Marker in Sydney") }
                        ?.let { mMap.addMarker(it) }
                    latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 16F) }
                        ?.let { mMap.moveCamera(it) }
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapsee) as SupportMapFragment
        mapFragment.getMapAsync(this)

        geofencingClient = LocationServices.getGeofencingClient(this)
        // get list of registered geofences
        //var listOfLocations = GeofencingRequest.Builder().get
        geofenceHelper = GeoFenceHelper(this)
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)
    }

    override fun onResume() {
        super.onResume()
        moveLocationButton();
    }

    private fun zoomOnMap(latLng: LatLng) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 16F)
        mMap.animateCamera(newLatLngZoom)
    }

    private fun moveLocationButton() {
        mapView = mapFragment.requireView()
        val myLocationButton: View =
            (mapView.findViewById<View>("1".toInt()).parent as View).findViewById<View>("2".toInt())

        if (myLocationButton.layoutParams is RelativeLayout.LayoutParams) {
            // location button is inside of RelativeLayout
            val params = myLocationButton.layoutParams as RelativeLayout.LayoutParams

            // Align it to - parent BOTTOM|LEFT
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)

            // Update margins, set to 80dp
            val margin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80f,
                getResources().displayMetrics
            ).toInt()
            params.setMargins(margin, margin, margin, margin)
            myLocationButton.layoutParams = params
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // TODO show current location
        // Add a marker in Sydney and move the camera
        //43.63278029804879, -79.48598033353436
        val home = LatLng(43.59505083084766, -79.53340835792551)
        mMap.addMarker(MarkerOptions().position(home).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 16F))

        enableUserLocation()

        // handle the long press event on the map
        mMap.setOnMapLongClickListener(this)
    }

    /*
    Method to get user current location (where am I)
     */
    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            // request for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_ACCESS_REQUEST_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    FINE_LOCATION_ACCESS_REQUEST_CODE
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission

                mMap.isMyLocationEnabled = true
            } else {
                //We do not have the permission.. add a case
            }
        }

        /*if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofence...", Toast.LENGTH_SHORT).show()
            } else {
                //We do not have the permission..
                Toast.makeText(
                    this,
                    "Background location access is necessary for geofence to trigger...",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }*/
    }

    override fun onMapLongClick(latLong: LatLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            println("CATCH - API above 29 Special")
            // we need background permission
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                handleMapLongClick(latLong)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                ) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf<String>(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf<String>(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
                    )
                }
            }
        } else {
            println("CATCH - NORMAL Procedure")
            handleMapLongClick(latLong)
        }
    }

    private fun handleMapLongClick(latLong: LatLng) {
        // enable only one marker at a time
        mMap.clear()
        // add marker and circle
        addMarkerForMapClick(latLong)
        addCircleForMarker(latLong, GEOFENCE_RADIUS)
        addGeofence(latLong, GEOFENCE_RADIUS)
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(latLong: LatLng, radius: Float) {
        // TODO check for already registered - https://stackoverflow.com/questions/6981916/how-to-calculate-distance-between-two-locations-using-their-longitude-and-latitu
        val transitionType: Int =
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
        val geofence: Geofence =
            geofenceHelper.getGeofence(GEOFENCE_ID, latLong, radius, transitionType)
        val geofencingRequest: GeofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val requestId = geofence.requestId
        val pendingIntent: PendingIntent = geofenceHelper.getPendingIntent

        //TODO
        if(geofencingRequest.geofences.contains(geofence)) {

        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent).run {
            addOnSuccessListener {
                Log.d(TAG, "onSuccess: GEOFENCE Added")
                println("CATCH - SUCCESS")
                Toast.makeText(this@MapsActivity, "GEOFENCE ADDED...", Toast.LENGTH_LONG).show()
                var addressLine: String? = ""
                var locality: String? = ""
                geocoder.getAddress(
                    latLong.latitude,
                    latLong.longitude
                ) { address: android.location.Address? ->
                    addressLine = address?.getAddressLine(0)
                    locality = address?.locality
                    println("THE ADDRESS: $addressLine")
                }

                // dialog box to display the address
                /*val alertDialogBox = AlertDialog.Builder(
                    ContextThemeWrapper(
                        this@MapsActivity,
                        androidx.appcompat.R.style.AlertDialog_AppCompat_Light
                    )
                )
                alertDialogBox.setTitle("Location Provider")
                    .setMessage(addressLine)
                    .setPositiveButton("OK") { dialog, _ ->
                        run {
                            dialog.dismiss()
                        }
                    }.show()*/


                // prepare the Location object
                val location = LocationDetails().apply {
                    latitude = latLong.latitude
                    longitude = latLong.longitude
                    address = addressLine.toString()
                    city = locality.toString()
                }

                // apply the composable
                composeView.apply {
                    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                    setContent {
                        val shouldShowDialog = remember { mutableStateOf(false) }
                        shouldShowDialog.value = true
                        getMyShoppingListTheme {
                            ShowAlertDialogToNameLocation(shouldShowDialog, location)
                        }
                    }
                }
            }
            addOnFailureListener { e ->
                val errorMessage: String = geofenceHelper.getErrorString(e)
                Log.d(TAG, "onFailure: $errorMessage")
                println("CATCH - FAILURE: ${e.message}")
            }
        }
    }

    private fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
        address: (android.location.Address?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) {
                address(it.firstOrNull())
            }
        } else {
            try {
                address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
            } catch (e: Exception) {
                // catch exception if there is internet problem
                println("EXCEPTION: ${e.message}")
                address(null)
            }
        }
    }

    private fun addMarkerForMapClick(latLng: LatLng) {
        val markerOptions = MarkerOptions().position(latLng)
        mMap.addMarker(markerOptions)
    }

    private fun addCircleForMarker(latLong: LatLng, radius: Float) {
        val circleOptions = CircleOptions()
        circleOptions.center(latLong)
        circleOptions.radius(radius.toDouble())
        circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
        circleOptions.fillColor(Color.argb(64, 255, 0, 0))
        circleOptions.strokeWidth(4f)
        mMap.addCircle(circleOptions)
    }

    @Composable
    private fun ShowAlertDialogToNameLocation(
        shouldShowDialog: MutableState<Boolean>,
        location: LocationDetails
    ) {

        var itemName by remember {
            mutableStateOf("")
        }
        if (shouldShowDialog.value) {
            AlertDialog(onDismissRequest = { shouldShowDialog.value = false }, confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        if (itemName.isNotBlank()) {
                            shouldShowDialog.value = false
                            // TODO Stuffs
                            location.apply {
                                locationName = itemName
                            }
                            itemName = ""
                            // TODO DB operation to store data
                            storeDataInRealm(location)
                        }
                    }) {
                        Text(text = "Add")
                    }
                    Button(onClick = {
                        shouldShowDialog.value = false
                        itemName = ""
                    }) {
                        Text(text = "Cancel")
                    }
                }
            },
                title = { Text(text = "Name the location") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = { itemName = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            )
        }
    }

    private fun storeDataInRealm(location: LocationDetails) {
        MainViewModel().addLocation(location)
        Log.d(
            TAG, "LocationDetails: ${location.locationName} -- " +
                    location.address + "--" + location.latitude + "--" +
                    location.longitude + "--" + location.city
        )
    }
}
