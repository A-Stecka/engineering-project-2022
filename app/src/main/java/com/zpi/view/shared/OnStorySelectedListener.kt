package com.zpi.view.shared

import com.zpi.model.entity.Story

interface OnStorySelectedListener {
    fun showFullStory(story: Story);
}