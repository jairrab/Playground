package com.example.temp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelData1(
    val a:Int,
    val b:String,
):Parcelable