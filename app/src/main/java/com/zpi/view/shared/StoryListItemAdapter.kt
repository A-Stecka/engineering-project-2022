package com.zpi.view.shared

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zpi.R
import com.zpi.model.entity.Story
import kotlin.math.min

class StoryListItemAdapter(private val onStorySelectedListener: OnStorySelectedListener? = null) :
    ListAdapter<Story, StoryListItemAdapter.StoryListItemHolder>(StoryItemDiff()) {

    val maxPreviewCharacters: Int = 500

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryListItemAdapter.StoryListItemHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.short_story_item, parent, false)
        return StoryListItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: StoryListItemHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class StoryListItemHolder(iv: View) : RecyclerView.ViewHolder(iv) {

        private val titleView: TextView = iv.findViewById(R.id.title_story_item)
        private val authorView: TextView = iv.findViewById(R.id.author_story_item)
        private val contentView: TextView = iv.findViewById(R.id.content_story_item)

        fun bindTo(story: Story) {
            titleView.text = story.title
            authorView.text = story.author
            val content = story.content.subSequence(0, min(maxPreviewCharacters, story.content.length)).toString() + "..."
            contentView.text = content
            itemView.setOnClickListener {
                onStorySelectedListener!!.showFullStory(story);
            }
        }
    }
}