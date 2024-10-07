package com.zpi.model.entity

import java.time.LocalDateTime

data class Score (
    val ref: Int,
    val value: Float,
    val regDate: LocalDateTime,
    val fkStory: Int,
    val fkUser: Int,
)
