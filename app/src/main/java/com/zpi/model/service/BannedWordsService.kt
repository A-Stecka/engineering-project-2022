package com.zpi.model.service

import android.util.Log
import com.zpi.model.Converter
import com.zpi.model.dto.BannedWordDTO
import com.zpi.model.entity.BannedWord
import com.zpi.model.repository.BannedWordsRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BannedWordsService {
    private var apiService: BannedWordsRepository = BannedWordsRepository.create(BannedWordsRepository::class.java)

    fun getBannedWords(callback: (bannedWords: MutableList<BannedWord>) -> Unit) {
        apiService.getBannedWords()?.enqueue(object : Callback<List<BannedWordDTO>?> {

            override fun onResponse(call: Call<List<BannedWordDTO>?>, response: Response<List<BannedWordDTO>?>) {
                Log.i("Get banned words called", "")
                if (response.body() != null) {
                    callback.invoke(Converter.convertToBannedWords(response.body()))
                } else {
                    callback.invoke(mutableListOf(BannedWord("","")))
                }
            }

            override fun onFailure(call: Call<List<BannedWordDTO>?>, t: Throwable) {
                callback.invoke(mutableListOf(BannedWord("","")))
            }
        })
    }
}