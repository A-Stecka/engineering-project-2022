package com.zpi.view.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zpi.R
import com.zpi.model.entity.StoriesPerGenre

class StoriesPerGenreAdapter : ListAdapter<StoriesPerGenre, StoriesPerGenreAdapter.StoriesPerGenreItemHolder>(StoriesPerGenreItemDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesPerGenreItemHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.stories_per_genre_item, parent, false)
        return StoriesPerGenreItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: StoriesPerGenreItemHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class StoriesPerGenreItemHolder(iv: View) : RecyclerView.ViewHolder(iv) {

        private val genre: TextView = iv.findViewById(R.id.stories_per_genre_genre)
        private val number: TextView = iv.findViewById(R.id.stories_per_genre_number)

        fun bindTo(storiesPerGenre: StoriesPerGenre) {
            genre.text = storiesPerGenre.genre
            number.text = storiesPerGenre.stories.toString()
        }
    }
}
