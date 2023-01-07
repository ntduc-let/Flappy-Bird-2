package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Map(
    var name: Int = 0,
    var res: Int = 0
) : Parcelable