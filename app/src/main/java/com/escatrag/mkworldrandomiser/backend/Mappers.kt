package com.escatrag.mkworldrandomiser.backend

import com.escatrag.mkworldrandomiser.backend.TrackItems

fun TrackItems.map(): Track {
    return Track(text = nameRes, icon = imgRes)
}

fun List<TrackItems>.map(): List<Track> {
    return map {
        it.map()
    }
}