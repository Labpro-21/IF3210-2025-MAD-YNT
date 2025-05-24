package com.ynt.purrytify.ui.screen.editprofilescreen.component

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun Maps(
    onLocationSelected: (String) -> Unit,
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    var locationText by remember { mutableStateOf("Belum ada lokasi") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedLocation by remember { mutableStateOf(LatLng(-6.88, 107.61)) }

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
                    selectedLocation = LatLng(location.latitude, location.longitude)
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

        val countryCode = getCountryCodeFromCoordinates(
            context,
            selectedLocation.latitude,
            selectedLocation.longitude
        )

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition(
                selectedLocation,
                15f,
                0f,
                0f
            )
        }

        LaunchedEffect(selectedLocation) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f)
            )
        }

        Row(
            modifier = Modifier.padding(top = 16.dp, bottom = 30.dp)
        ) {
            Text(
                text = "Location",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )

            if (countryCode != null) {
                onLocationSelected(countryCode)
            }

            Text(
                text = countryCode ?: "-",
                modifier = Modifier.padding(start = 60.dp),
                color = Color.White,
                fontSize = 18.sp
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari lokasi") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 5.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(
                        topStart = 8.dp,
                        bottomStart = 8.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                )

                IconButton(
                    onClick = {
                        if (searchQuery != "") {
                            focusManager.clearFocus()
                            coroutineScope.launch {
                                val geocoder = Geocoder(context, Locale.getDefault())
                                val addressList = withContext(Dispatchers.IO) {
                                    geocoder.getFromLocationName(searchQuery, 1)
                                }

                                if (!addressList.isNullOrEmpty()) {
                                    val address = addressList[0]
                                    selectedLocation = LatLng(address.latitude, address.longitude)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .height(55.dp)
                        .width(55.dp)
                        .clip(RoundedCornerShape(
                            topStart = 0.dp,
                            bottomStart = 0.dp,
                            topEnd = 8.dp,
                            bottomEnd = 8.dp
                        ))
                        .background(Color(0xFF4CAF50))
                        .padding(horizontal = 16.dp)
                    ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari lokasi",
                        tint = Color.White
                    )
                }
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
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
