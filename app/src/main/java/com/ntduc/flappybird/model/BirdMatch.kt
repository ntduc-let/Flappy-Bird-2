package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BirdMatch(
    var birdX: Float = 0f,
    var birdY: Float = 0f,
    var velocity: Int = 0,
    var gravity: Int = 2
) : Parcelable