package com.zpi.view.story

import com.zpi.model.entity.Comment

interface OnCommentSelectedListener {
    fun deleteComment(comment: Comment)
}