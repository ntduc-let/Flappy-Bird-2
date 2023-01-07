package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Info(
    var uid: String = "",
    var name: String = "",
    var photoUrl: String = "",
    var email: String = ""
) : Parcelable