package com.zpi.model.service

import com.zpi.model.Converter
import com.zpi.model.dto.GenreDTO
import com.zpi.model.entity.Genre
import com.zpi.model.repository.GenreRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenreService {
    private var apiService: GenreRepository = GenreRepository.create(GenreRepository::class.java)

    fun getGenres(callback: (genres: List<Genre>) -> Unit) {
        apiService.getGenres()?.enqueue(object : Callback<List<GenreDTO>?> {

            override fun onResponse(call: Call<List<GenreDTO>?>, response: Response<List<GenreDTO>?>) {
                if (response.body() != null) {
                    val genres: MutableList<Genre> = Converter.convertToGenreList(response.body()!!)
                    callback.invoke(genres)
                } else {
                    callback.invoke(listOf(Genre(-1,"")))
                }
            }

            override fun onFailure(call: Call<List<GenreDTO>?>, t: Throwable) {
                callback.invoke(listOf(Genre(-1,"")))
            }
        })
    }
}