package com.zpi.view.profile

import androidx.recyclerview.widget.DiffUtil
import com.zpi.model.entity.StoriesPerGenre

class StoriesPerGenreItemDiff : DiffUtil.ItemCallback<StoriesPerGenre>() {

    override fun areItemsTheSame(oldItem: StoriesPerGenre, newItem: StoriesPerGenre): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: StoriesPerGenre, newItem: StoriesPerGenre): Boolean {
        return oldItem == newItem
    }
}
