package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Bird(
    var name: String = "",
    var src: Int = 0,
    var res1: Int = 0,
    var res2: Int = 0
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        return this.src == (other as Bird).src
    }
}