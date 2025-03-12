package com.zj.mobileTest.data

import android.content.Context
import com.zj.mobileTest.data.model.Booking
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DataManager {
    private lateinit var bookingService: BookingService

    fun init(context: Context) {
        bookingService = BookingService(context)
    }

    suspend fun getBookingData(refresh: Boolean): Result<Booking> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(bookingService.getBookingData(refresh))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}    