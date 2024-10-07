package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class PublishStoryDTO (
    @SerializedName("title") var title: String? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("reg_date") var regDate: String? = null,
    @SerializedName("fk_user") var fkUser: Int? = null,
    @SerializedName("fk_prompt") var fkPrompt: Int? = null
)