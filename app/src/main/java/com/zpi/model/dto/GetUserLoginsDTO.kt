package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

class GetUserLoginsDTO {
    @SerializedName("logins") var logins: String? = null
}