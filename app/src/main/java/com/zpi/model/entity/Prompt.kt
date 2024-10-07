package com.zpi.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Prompt (
    val ref: Int,
    val genre: String,
    val words: List<String>
) : Parcelable {

     fun getWordsString(): String {
        var wordsString = ""
        for (word in words) {
            wordsString += "$word, "
        }
        return wordsString.dropLast(2)
    }
}
