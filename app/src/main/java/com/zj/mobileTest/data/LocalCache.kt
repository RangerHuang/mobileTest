package com.zj.mobileTest.data

import android.content.Context
import com.zj.mobileTest.data.model.Booking
import com.zj.mobileTest.data.model.Location
import com.zj.mobileTest.data.model.OriginAndDestinationPair
import com.zj.mobileTest.data.model.Segment
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

class LocalCache(private val context: Context) {
    private val cacheFileName = "booking_cache.json"

    fun getCachedData(): Booking {
        val cacheFile = File(context.filesDir, cacheFileName)
        if (cacheFile.exists()) {
            val jsonText = cacheFile.readText()
            val jsonObject = JSONObject(jsonText)

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
        return Booking()
    }

    fun saveDataToCache(booking: Booking) {
        val jsonObject = JSONObject()
        jsonObject.put("shipReference", booking.shipReference)
        jsonObject.put("shipToken", booking.shipToken)
        jsonObject.put("canIssueTicketChecking", booking.canIssueTicketChecking)
        jsonObject.put("expiryTime", booking.expiryTime)
        jsonObject.put("duration", booking.duration)

        val segmentsArray = JSONArray()
        booking.segments.forEach { segment ->
            val segmentObj = JSONObject()
            segmentObj.put("id", segment.id)

            val pairObj = JSONObject()
            val destinationObj = JSONObject()
            destinationObj.put("code", segment.originAndDestinationPair.destination.code)
            destinationObj.put("displayName", segment.originAndDestinationPair.destination.displayName)
            destinationObj.put("url", segment.originAndDestinationPair.destination.url)
            pairObj.put("destination", destinationObj)

            pairObj.put("destinationCity", segment.originAndDestinationPair.destinationCity)

            val originObj = JSONObject()
            originObj.put("code", segment.originAndDestinationPair.origin.code)
            originObj.put("displayName", segment.originAndDestinationPair.origin.displayName)
            originObj.put("url", segment.originAndDestinationPair.origin.url)
            pairObj.put("origin", originObj)

            pairObj.put("originCity", segment.originAndDestinationPair.originCity)
            segmentObj.put("originAndDestinationPair", pairObj)
            segmentsArray.put(segmentObj)
        }
        jsonObject.put("segments", segmentsArray)

        val cacheFile = File(context.filesDir, cacheFileName)
        FileWriter(cacheFile).use { writer ->
            writer.write(jsonObject.toString())
        }
    }
}    