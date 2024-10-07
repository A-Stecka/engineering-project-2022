package com.zpi.model.dto

import com.google.gson.annotations.SerializedName

data class StoryAnalysisDTO(
    @SerializedName("correctness_points"        ) var correctnessPoints       : String? = null,
    @SerializedName("genre"                     ) var genre                   : String? = null,
    @SerializedName("genre_points"              ) var genrePoints             : String? = null,
    @SerializedName("negativeness_score"        ) var negativenessScore       : String? = null,
    @SerializedName("neutralness_score"         ) var neutralnessScore        : String? = null,
    @SerializedName("positiveness_score"        ) var positivenessScore       : String? = null,
    @SerializedName("prompt_completion_points"  ) var promptCompletionPoints  : String? = null,
    @SerializedName("vocabulary_variety_points" ) var vocabularyVarietyPoints : String? = null
)
