package com.example.navermapsample

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.example.navermapsample.databinding.ActivityMainBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource


class MainActivity : ComponentActivity() {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1004
        private val PERMISSIONS = arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun initMapView() {
        binding.stationMap.getMapAsync {
            this.naverMap = it

            locationSource =
                FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
            this.naverMap.locationSource = locationSource
            this.naverMap.uiSettings.isLocationButtonEnabled = true
            this.naverMap.locationTrackingMode = LocationTrackingMode.Follow
        }
    }

    //권한 확인
    private fun hasPermission(): Boolean {
        return PermissionChecker.checkSelfPermission(this, PERMISSIONS[0]) ==
                PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, PERMISSIONS[1]) ==
                PermissionChecker.PERMISSION_GRANTED
    }

    //권한 확인 후 처리
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                val text = binding.tvMessage.text.toString() + "\nACCESS_FINE_LOCATION\n"
                binding.tvMessage.text = text
                initMapView()
            }

            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                val text = binding.tvMessage.text.toString() + "\nACCESS_COARSE_LOCATION\n"
                binding.tvMessage.text = text
                initMapView()

            }

            else -> {
                val text = binding.tvMessage.text.toString() + "\n권한 없음\n"
                binding.tvMessage.text = text
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!hasPermission()) {
            locationPermissionRequest.launch(PERMISSIONS)
        } else {
            initMapView()
        }
    }
}