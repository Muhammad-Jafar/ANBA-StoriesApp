package jafar.stories.features.main.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jafar.stories.R
import jafar.stories.data.model.AddStoryLocation
import jafar.stories.databinding.ActivityPickLocationStoryBinding
import jafar.stories.features.main.AddStoryViewModel
import jafar.stories.utils.Constanta
import jafar.stories.utils.ViewModelFactory
import jafar.stories.utils.parseAddressLocation

class PickLocationStoryActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityPickLocationStoryBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickLocationStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.addStoryToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        supportActionBar?.elevation = 0f

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        val mapFragment =
            (supportFragmentManager.findFragmentById(R.id.mapsAddLocationStory) as SupportMapFragment)
        mapFragment.getMapAsync(this)
        setupViewModel()

        binding.getLocationButton.setOnClickListener { getMyRecentLocation() }

        binding.chooseLocationButton.setOnClickListener {
            (viewModel as AddStoryViewModel).let {
                val pickState = it.isLocationPicked.value
                val lat = it.latitude.value
                val lon = it.longitude.value
                val data = AddStoryLocation(pickState, lat, lon)

                if (pickState == true) {
                    Intent(this, FromAddStoryActivity::class.java).also { intent ->
                        intent.putExtra(Constanta.EXTRA_COORDINATE, data)
                        setResult(RESULT_OK)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Intent(this, FromAddStoryActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                        Toast.makeText(this, "No location picked", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getStoryInstance(this)
        val addStoryViewModel: AddStoryViewModel by viewModels { factory }
        viewModel = addStoryViewModel
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Constanta.indonesiaLocation, 4f))
        getMyRecentLocation()

        mMap.setOnPoiClickListener {
            mMap.clear()
            val position = LatLng(it.latLng.latitude, it.latLng.longitude)
            mMap.addMarker(MarkerOptions().position(position).snippet(it.name))?.showInfoWindow()
            val address =
                parseAddressLocation(this, it.latLng.latitude, it.latLng.longitude)
            binding.chooseLocationName.text = it.name
            binding.chooseLocationAddress.text = address
            (viewModel as AddStoryViewModel).let { data ->
                data.isLocationPicked.postValue(true)
                data.latitude.postValue(it.latLng.latitude)
                data.longitude.postValue(it.latLng.longitude)
            }
        }
    }

    private fun pickSelectedLocation(lat: Double, lon: Double) {
        binding.apply {
            val address = parseAddressLocation(this@PickLocationStoryActivity, lat, lon)
            chooseLocationName.text = getString(R.string.my_location)
            chooseLocationAddress.text = address
        }
        (viewModel as AddStoryViewModel).let {
            it.isLocationPicked.postValue(true)
            it.latitude.postValue(lat)
            it.longitude.postValue(lon)
        }
    }

    private fun getMyRecentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    mMap.isMyLocationEnabled = true
                    val position = LatLng(it.latitude, it.longitude)
                    mMap.addMarker(MarkerOptions().position(position))
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 16f))
                    pickSelectedLocation(it.latitude, it.longitude)
                }
            }
        } else requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).toString()
        )
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) getMyRecentLocation()
        else {
            MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.title_location_info))
                .setMessage(resources.getString(R.string.message_location_info))
                .setPositiveButton(resources.getString(R.string.button_ok)) { dialog, _ ->
                    dialog.dismiss()
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}