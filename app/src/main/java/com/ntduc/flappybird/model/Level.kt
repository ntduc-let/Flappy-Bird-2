package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Level(
    var level: Int = 1,
    var name: String = "",
    var bestCoin: Int = 0
) : Parcelable