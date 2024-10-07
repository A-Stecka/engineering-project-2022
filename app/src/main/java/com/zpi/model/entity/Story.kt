package com.zpi.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Story (
    val ref: Int,
    val title: String,
    val author: String,
    val content: String,
    val regDate: LocalDateTime,
    val fkUser: Int,
    val prompt: Prompt
) : Parcelable