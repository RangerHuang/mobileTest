package com.zj.mobileTest.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.semantics.SemanticsProperties.Text
import androidx.compose.ui.unit.dp

import com.zj.mobileTest.data.DataManager
import com.zj.mobileTest.data.model.Booking
import com.zj.mobileTest.viewmodel.BookingViewModel
import kotlinx.coroutines.launch

import androidx.compose.material.Text
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun BookingListScreen() {
    val viewModel: BookingViewModel = BookingViewModel()
    var booking by remember { mutableStateOf<Booking?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        val result = DataManager.getBookingData(refresh = false)
        isLoading = false
        if (result.isSuccess) {
            booking = result.getOrThrow()
            viewModel.printBookingData(booking!!)
        } else {
            error = result.exceptionOrNull()?.message
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                isLoading = true
                coroutineScope.launch {
                    val result = DataManager.getBookingData(refresh = true)
                    isLoading = false
                    if (result.isSuccess) {
                        booking = result.getOrThrow()
                        viewModel.printBookingData(booking!!)
                        error = null
                    } else {
                        error = result.exceptionOrNull()?.message
                    }
                }
            },
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text("刷新数据")
        }

        if (isLoading) {
            CircularProgressIndicator()//显示圆形进度条
        } else if (error != null) {
            Text("加载数据出错: $error")
        } else if (booking != null) {
            LazyColumn {
                items(booking!!.segments) { segment ->
                    Text(text = "Segment ID: ${segment.id}")
                    Text(text = "Origin: ${segment.originAndDestinationPair.origin.displayName}")
                    Text(text = "Destination: ${segment.originAndDestinationPair.destination.displayName}")
                }
            }
        }
    }
}