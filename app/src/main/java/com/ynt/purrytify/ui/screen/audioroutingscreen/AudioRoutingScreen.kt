package com.ynt.purrytify.ui.screen.audioroutingscreen

import android.content.Context
import android.media.AudioDeviceCallback
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material3.Divider
import androidx.compose.foundation.layout.size
import com.ynt.purrytify.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.ynt.purrytify.ui.screen.player.PlaybackViewModel

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun AudioRoutingScreen(
    playbackViewModel: PlaybackViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    val outputDevicesState = remember { mutableStateOf(listOf<AudioDeviceInfo>()) }

    DisposableEffect(audioManager) {
        val callback = object : AudioDeviceCallback() {
            override fun onAudioDevicesAdded(addedDevices: Array<out AudioDeviceInfo>) {
                updateOutputDevices()
            }

            override fun onAudioDevicesRemoved(removedDevices: Array<out AudioDeviceInfo>) {
                updateOutputDevices()
            }

            fun updateOutputDevices() {
                val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS).filter {
                    when (it.type) {
                        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
                        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER,
                        AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
                        AudioDeviceInfo.TYPE_WIRED_HEADSET,
                        AudioDeviceInfo.TYPE_USB_HEADSET -> true
                        else -> false
                    }
                }
                outputDevicesState.value = devices
            }
        }

        callback.updateOutputDevices()
        audioManager.registerAudioDeviceCallback(callback, null)

        onDispose {
            audioManager.unregisterAudioDeviceCallback(callback)
        }
    }

    var selectedDeviceId by remember {
        mutableStateOf(playbackViewModel.selectedDeviceId.value)
    }

    LaunchedEffect(outputDevicesState.value) {
        val savedId = playbackViewModel.selectedDeviceId.value
        val savedDevice = outputDevicesState.value.find { it.id == savedId }
        if (savedDevice != null) {
            selectedDeviceId = savedDevice.id
            playbackViewModel.setPreferredOutputDevice(savedDevice)
        } else {
            val builtInSpeaker = outputDevicesState.value.find { it.type == AudioDeviceInfo.TYPE_BUILTIN_SPEAKER }
            builtInSpeaker?.let {
                selectedDeviceId = it.id
                playbackViewModel.setPreferredOutputDevice(it)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(top = 25.dp, start = 18.dp, end = 18.dp, bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Audio Routes",
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        outputDevicesState.value.forEach { device ->
            val isSelected = selectedDeviceId == device.id
            val textColor = if (isSelected) colorResource(R.color.green) else Color.White
            val connectedStatus = if (isSelected) "Connected" else "Disconnected"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedDeviceId = device.id
                        playbackViewModel.setPreferredOutputDevice(device)
                    }
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = device.productName?.toString() ?: "Unknown",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = textColor
                    )
                    Text(
                        text = getDeviceTypeName(device.type),
                        fontSize = 14.sp,
                        color = textColor
                    )
                    Text(
                        text = connectedStatus,
                        fontSize = 14.sp,
                        color = textColor
                    )
                    if (isSelected) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Chosen Audio Route",
                            tint = colorResource(R.color.green),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            Divider(color = Color.Gray)
        }
    }
}

fun getDeviceTypeName(type: Int): String {
    return when (type) {
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "Bluetooth A2DP"
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Built-in Speaker"
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "Wired Headphones"
        AudioDeviceInfo.TYPE_WIRED_HEADSET -> "Wired Headset"
        AudioDeviceInfo.TYPE_USB_HEADSET -> "USB Headset"
        else -> "Other"
    }
}
