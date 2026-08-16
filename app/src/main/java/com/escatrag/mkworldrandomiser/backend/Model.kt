package com.escatrag.mkworldrandomiser.backend

data class TrackCombo(
    val start: TrackItems,
    val end: TrackItems? = null
) {
    val type: TrackComboType
        get() = if (end == null) TrackComboType.TRACK else TrackComboType.CONNECTION
}

enum class TrackComboType {
    TRACK,
    CONNECTION
}
