package com.zj.mobileTest.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.zj.mobileTest.data.model.Booking

class BookingViewModel : ViewModel() {
    fun printBookingData(booking: Booking) {
        Log.d("BookingData", booking.toString())
    }
}    