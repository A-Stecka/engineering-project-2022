package com.zpi.model.entity

import java.time.LocalDateTime

data class User (
    val ref: Int,
    val login: String,
    val password: String,
    val name: String,
    val profilePicture: Int,
    val regDate: LocalDateTime,
    val userType: Int,
    val email: String
)