package jafar.stories.features.main.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import jafar.stories.R
import jafar.stories.data.model.ListStory
import jafar.stories.data.repository.Result
import jafar.stories.databinding.FragmentMapsBinding
import jafar.stories.databinding.InfoWindowMapsBinding
import jafar.stories.features.main.MapViewModel
import jafar.stories.utils.Constanta
import jafar.stories.utils.ViewModelFactory
import jafar.stories.utils.showAlertLoading

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: ViewModel
    private lateinit var loadingBar: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        loadingBar = showAlertLoading(requireActivity())
        loadingBar.dismiss()
        setupViewModel()

        val mapFragment =
            (childFragmentManager.findFragmentById(R.id.fragment_maps_container) as SupportMapFragment)
        mapFragment.getMapAsync(this)
        return binding.root
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getStoryInstance(requireContext())
        val viewModel: MapViewModel by viewModels { factory }
        this.viewModel = viewModel
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        setMapStyle()

        val countryLocation = Constanta.indonesiaLocation
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(countryLocation, 4f))

        (viewModel as MapViewModel).getStoriesLocation().observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            when (result) {
                is Result.Loading -> loadingBar.show()
                is Result.Error -> {
                    loadingBar.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.network_unavailable),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Result.Success -> {
                    loadingBar.dismiss()
                    val dataStories = result.data
                    dataStories.listStory.forEach { listData ->
                        val location =
                            LatLng(listData.lat?.toDouble()!!, listData.lon?.toDouble()!!)
                        mMap.addMarker(MarkerOptions().position(location))?.tag = listData
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 4f))
                    }
                }
            }
        }
        mMap.setInfoWindowAdapter(this)
        mMap.setOnInfoWindowClickListener { mark ->
            val data: ListStory = mark.tag as ListStory
            getDetailMarkPoint(data)
        }
    }

    private fun getDetailMarkPoint(data: ListStory) {
        val dataMarkPoint = DetailMarkPointFragment()
        val mBundle = Bundle()
        mBundle.putParcelable(Constanta.EXTRA_DATA, data)
        dataMarkPoint.arguments = mBundle
        dataMarkPoint.show(childFragmentManager, DetailMarkPointFragment::class.java.simpleName)
    }

    private fun setMapStyle() {
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireActivity(), R.raw.map_style
            )
        )
    }

    override fun getInfoContents(marker: Marker): View? = null

    override fun getInfoWindow(marker: Marker): View {
        val bindingDetailMarkPoint =
            InfoWindowMapsBinding.inflate(LayoutInflater.from(requireContext()))
        val data: ListStory = marker.tag as ListStory
        bindingDetailMarkPoint.markPointName.text = StringBuilder("Story by ").append(data.name)
        return bindingDetailMarkPoint.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
