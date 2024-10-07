package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class GetUserPasswordDTO(
    @SerializedName("ref") var ref: Int? = null,
    @SerializedName("password") var password: String? = null
)
