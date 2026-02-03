package com.example.iptv

import java.io.Serializable

data class Channel(
    val name: String,
    val url: String,
    val streamType: StreamType = StreamType.UDP,
    val channelNumber: Int = 0,
    //var isFavorite: Boolean = false, // Add this
    val logoUrl: String? = null, // For future channel logos
    //val category: String = "General" // For grouping
) : Serializable {

    enum class StreamType {
        UDP,
        RTP,
        HTTP
    }

    override fun toString(): String {
        return "Channel(name='$name', url='$url', type=$streamType)"
    }
}