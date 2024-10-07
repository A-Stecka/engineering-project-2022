package com.zpi.model.entity

import java.time.LocalDateTime

data class Comment(
    val REF: Int,
    val content: String,
    val regDate: LocalDateTime,
    val username: String,
    val profilePicture: Int,
    val fkUser: Int,
    val fkStory: Int
)
