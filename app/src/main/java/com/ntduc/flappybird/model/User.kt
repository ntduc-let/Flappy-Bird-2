package com.ntduc.flappybird.model

import android.os.Parcelable
import com.ntduc.flappybird.model.*
import com.ntduc.flappybird.repository.Repository
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    var info: Info? = null,
    var bird: Bird = Repository.getListBird()[0],
    var level: Level = Level(),
    var coin: Int = 0,
    var match: Match = Match()
) : Parcelable