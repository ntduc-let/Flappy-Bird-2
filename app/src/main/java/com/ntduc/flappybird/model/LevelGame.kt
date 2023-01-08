package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LevelGame(
    var id: Int = 0,
    var level: Int = 0,
    var score: Int = 0,
    var isPassed: Boolean = false
) : Parcelable