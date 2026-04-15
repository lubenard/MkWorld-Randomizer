package com.escatrag.mkworldrandomiser.backend

fun TrackItems.map(): Track {
    return Track(text = nameRes, icon = imgRes)
}

fun List<TrackItems>.map(): List<TrackCombo> {
    return map {
        TrackCombo(
            start = it.map(),
            type = TrackComboType.TRACK
        )
    }
}