package com.zpi.view.story

import androidx.recyclerview.widget.DiffUtil
import com.zpi.model.entity.Comment

class CommentItemDiff : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }
}