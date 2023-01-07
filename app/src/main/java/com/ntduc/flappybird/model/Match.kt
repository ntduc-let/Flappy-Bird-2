package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Match(
    var isCreated: Boolean = false,
    var isConnected: Boolean = false,
    var isPlaying: Boolean = false,
    var rival: Rival? = null,
    var statusGame: Int = 0,
    var scoreGame: Int = 0,
    var coin: Int = 0
) : Parcelable