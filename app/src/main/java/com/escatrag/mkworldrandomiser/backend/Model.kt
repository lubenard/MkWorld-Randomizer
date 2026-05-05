package com.escatrag.mkworldrandomiser.backend

data class Track(
    val text: Int, // Resource
    val icon: Int, // Destination resource
    val largeIcon: Int
)

data class TrackCombo(
    val start: Track,
    val end: Track? = null,
    val type: TrackComboType
)

enum class TrackComboType {
    TRACK,
    CONNECTION
}
