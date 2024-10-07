package com.zpi.view.shared

import androidx.recyclerview.widget.DiffUtil
import com.zpi.model.entity.Story

class StoryItemDiff : DiffUtil.ItemCallback<Story>() {

    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
}