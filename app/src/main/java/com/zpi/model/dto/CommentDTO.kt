package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class CommentDTO(
    @SerializedName("ref") var commentRef: Int? = null,
    @SerializedName("content") var content: String? = null,
    @SerializedName("reg_date") var regDate: String? = null,
    @SerializedName("fk_user") var fkUser: Int? = null,
    @SerializedName("fk_story") var fkStory: Int? = null,
    @SerializedName("name") var username: String? = null,
    @SerializedName("profile_pic") var profilePicture: Int? = null
)
