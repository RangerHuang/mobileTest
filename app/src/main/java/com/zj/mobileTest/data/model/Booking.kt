package com.zj.mobileTest.data.model

data class Booking(
    val shipReference: String = "",
    val shipToken: String = "",
    val canIssueTicketChecking: Boolean = false,
    val expiryTime: String = "",
    val duration: Int = 0,
    val segments: List<Segment> = emptyList()
)

data class Segment(
    val id: Int,
    val originAndDestinationPair: OriginAndDestinationPair
)

data class OriginAndDestinationPair(
    val destination: Location,
    val destinationCity: String,
    val origin: Location,
    val originCity: String
)

data class Location(
    val code: String,
    val displayName: String,
    val url: String
)    