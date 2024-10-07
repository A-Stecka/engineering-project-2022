package com.zpi.model.service

import android.util.Log
import com.zpi.model.Converter
import com.zpi.model.dto.PromptDTO
import com.zpi.model.entity.Prompt
import com.zpi.model.repository.PromptRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PromptService {
    private var apiService: PromptRepository = PromptRepository.create(PromptRepository::class.java)

    fun generateChallenge(noOfWords: Int, callback: (prompt: Prompt) -> Unit) {
        apiService.generateChallenge(noOfWords)?.enqueue(object : Callback<PromptDTO?> {

            override fun onResponse(call: Call<PromptDTO?>, response: Response<PromptDTO?>) {
                Log.i("Generate challenge called", "")
                if (response.body() != null) {
                    Log.i("Prompt service returned", response.body().toString())
                    val prompt: Prompt = Converter.convertToPrompt(response.body())
                    callback.invoke(prompt)
                } else {
                    callback.invoke(Prompt(-10,"", emptyList()))
                }
            }

            override fun onFailure(call: Call<PromptDTO?>, t: Throwable) {
                Log.e("Generate challenge failed", "")
                callback.invoke(Prompt(-10,"", emptyList()))
            }
        })
    }
}