package com.ntduc.flappybird.model

import android.os.Parcelable
import com.ntduc.flappybird.model.*
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var info: Info? = null,
    var bird: Bird? = null,
    var level: Level? = null,
    var coin: Int = 0,
    var match: Match? = null
) : Parcelable