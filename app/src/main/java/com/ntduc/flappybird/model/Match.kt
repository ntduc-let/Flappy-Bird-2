package com.ntduc.flappybird.model

import android.os.Parcelable
import com.ntduc.flappybird.repository.Repository.STATE_GAME_PREPARE
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Match(
    var isCreated: Boolean = false,
    var isConnected: Boolean = false,
    var isPlaying: Boolean = false,
    var rival: User? = null,
    var statusGame: Int = STATE_GAME_PREPARE,
    var scoreGame: Int = 0,
    var coin: Int = 0
) : Parcelable