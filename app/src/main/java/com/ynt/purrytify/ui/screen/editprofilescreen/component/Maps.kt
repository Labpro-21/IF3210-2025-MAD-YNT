package com.ynt.purrytify.ui.screen.editprofilescreen.component

import android.Manifest
import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Maps() {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    var userLatitude by remember { mutableStateOf<Double?>(null) }
    var userLongitude by remember { mutableStateOf<Double?>(null) }
    var locationText by remember { mutableStateOf("Belum ada lokasi") }
    var searchQuery by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    if (permissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    locationText = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                    userLatitude = location.latitude
                    userLongitude = location.longitude
                }
            }
        }
    } else if (permissionState.status.shouldShowRationale) {
        locationText = "Aplikasi membutuhkan izin lokasi"
    } else {
        locationText = "Izin lokasi belum diberikan"
    }


    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        var selectedLocation by remember { mutableStateOf(LatLng(-6.88, 107.61)) }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition(
                selectedLocation,
                15f,
                0f,
                0f
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Cari lokasi") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            Button(onClick = {
                focusManager.clearFocus()
                coroutineScope.launch {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addressList = withContext(Dispatchers.IO) {
                        geocoder.getFromLocationName(searchQuery, 1)
                    }

                    if (!addressList.isNullOrEmpty()) {
                        val address = addressList[0]
                        selectedLocation = LatLng(address.latitude, address.longitude)
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    address.latitude,
                                    address.longitude
                                ), 15f
                            )
                        )
                    }
                }
            }) {
                Text("Cari")
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    selectedLocation = it
                }
            ) {
                Marker(state = MarkerState(position = selectedLocation), title = "Lokasi dipilih")
            }

            Text(
                text = "Koordinat: ${selectedLocation.latitude}, ${selectedLocation.longitude}",
                modifier = Modifier.padding(16.dp),
                color = Color.White
            )

            val countryCode = getCountryCodeFromCoordinates(
                context,
                selectedLocation.latitude,
                selectedLocation.longitude
            )

            if (countryCode != null) {
                Text(
                    text = countryCode,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White
                )
            }
        }
    }

}

fun getCountryCodeFromCoordinates(context: Context, latitude: Double, longitude: Double): String? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0].countryCode
        } else null
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
