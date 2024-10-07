package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("ref") var ref: Int? = null,
    @SerializedName("login") var login: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("profile_pic") var profilePicture: Int? = null,
    @SerializedName("reg_date") var regDate: String? = null,
    @SerializedName("user_type") var userType: Int? = null,
    @SerializedName("email") var email: String? = null
)