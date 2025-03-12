package com.zj.mobileTest.data

import android.content.Context
import com.zj.mobileTest.data.model.Booking
import com.zj.mobileTest.data.model.Location
import com.zj.mobileTest.data.model.OriginAndDestinationPair
import com.zj.mobileTest.data.model.Segment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader

class BookingService(private val context: Context) {
    private val localCache = LocalCache(context)
    private val dataTTL = 3600 * 1000L // 数据有效期 1 小时

    suspend fun getBookingData(refresh: Boolean): Booking {
        return withContext(Dispatchers.IO) {
            val cachedData = localCache.getCachedData()
            if (!refresh && isCacheValid(cachedData)) {
                cachedData
            } else {
                val newData = loadDataFromJsonFile()
                localCache.saveDataToCache(newData)
                newData
            }
        }
    }

    private fun isCacheValid(booking: Booking): Boolean {
        if (booking.expiryTime.equals("")){
            return false
        }
        val expiryTime = booking.expiryTime.toLong() * 1000
        return System.currentTimeMillis() < expiryTime
    }

    private fun loadDataFromJsonFile(): Booking {
        val inputStream = context.assets.open("booking.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val jsonText = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            jsonText.append(line)
        }
        val jsonObject = JSONObject(jsonText.toString())

        val shipReference = jsonObject.getString("shipReference")
        val shipToken = jsonObject.getString("shipToken")
        val canIssueTicketChecking = jsonObject.getBoolean("canIssueTicketChecking")
        val expiryTime = jsonObject.getString("expiryTime")
        val duration = jsonObject.getInt("duration")

        val segmentsArray = jsonObject.getJSONArray("segments")
        val segments = mutableListOf<Segment>()
        for (i in 0 until segmentsArray.length()) {
            val segmentObj = segmentsArray.getJSONObject(i)
            val segmentId = segmentObj.getInt("id")

            val pairObj = segmentObj.getJSONObject("originAndDestinationPair")
            val destinationObj = pairObj.getJSONObject("destination")
            val destination = Location(
                code = destinationObj.getString("code"),
                displayName = destinationObj.getString("displayName"),
                url = destinationObj.getString("url")
            )
            val destinationCity = pairObj.getString("destinationCity")

            val originObj = pairObj.getJSONObject("origin")
            val origin = Location(
                code = originObj.getString("code"),
                displayName = originObj.getString("displayName"),
                url = originObj.getString("url")
            )
            val originCity = pairObj.getString("originCity")

            val pair = OriginAndDestinationPair(
                destination = destination,
                destinationCity = destinationCity,
                origin = origin,
                originCity = originCity
            )

            val segment = Segment(
                id = segmentId,
                originAndDestinationPair = pair
            )
            segments.add(segment)
        }

        return Booking(
            shipReference = shipReference,
            shipToken = shipToken,
            canIssueTicketChecking = canIssueTicketChecking,
            expiryTime = expiryTime,
            duration = duration,
            segments = segments
        )
    }
}    