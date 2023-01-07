package com.ntduc.flappybird.model

import android.os.Parcelable
import com.ntduc.flappybird.model.*
import com.ntduc.flappybird.repository.Repository
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rival(
    var info: Info? = null,
    var bird: Bird = Repository.getListBird()[0],
    var statusGame: Int = 0,
    var scoreGame: Int = 0,
    var coin: Int = 0
) : Parcelable