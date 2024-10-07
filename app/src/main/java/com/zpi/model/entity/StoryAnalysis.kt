package com.zpi.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryAnalysis(
    var correctnessPoints: Double?,
    var promptCompletionPoints: Double?,
    var vocabularyVarietyPoints: Double?,
    var genrePoints: Double?,
    var genre: String?,
    var positivenessScore: String?,
    var negativenessScore: String?,
    var neutralnessScore: String?
) : Parcelable