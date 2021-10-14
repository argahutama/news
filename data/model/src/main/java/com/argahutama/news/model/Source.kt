package com.argahutama.news.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Source(@SerializedName("name") val name: String?) : Parcelable
