package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class PublishCommentDTO (
    @SerializedName("content") var content: String? = null,
    @SerializedName("reg_date") var regdate: String? = null,
    @SerializedName("fk_user") var fkUser: Int? = null,
    @SerializedName("fk_story") var fkStory: Int? = null
)
