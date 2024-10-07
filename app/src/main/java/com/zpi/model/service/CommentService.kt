package com.zpi.model.service

import android.util.Log
import com.zpi.model.Converter
import com.zpi.model.dto.*
import com.zpi.model.entity.Comment
import com.zpi.model.repository.CommentRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class CommentService {
    private var apiService: CommentRepository =
        CommentRepository.create(CommentRepository::class.java)

    fun getStoryComments(storyREF: Int, callback: (comments: List<Comment>) -> Unit) {
        apiService.getStoryComments(storyREF)?.enqueue(object : Callback<List<CommentDTO>?> {
            override fun onResponse(call: Call<List<CommentDTO>?>, response: Response<List<CommentDTO>?>) {
                Log.i("Get story comments called for story", storyREF.toString())
                if (response.body() != null) {
                    val comments: MutableList<Comment> = Converter.convertToCommentList(response.body())
                    Log.i("Comments service returned", response.body().toString())
                    callback.invoke(comments)
                } else {
                    callback.invoke(listOf(Comment(-1,"", LocalDateTime.now(), "", 0, 0, 0)))
                }
            }

            override fun onFailure(call: Call<List<CommentDTO>?>, t: Throwable) {
                callback.invoke(listOf(Comment(-1,"", LocalDateTime.now(), "", 0, 0, 0)))
            }

        })
    }

    fun publishComment(comment: Comment, callback: (success: Boolean, mode:Boolean) -> Unit) {
        val commentDTO = Converter.convertToCommentDTO(comment)
        Log.e("CommentDTO", commentDTO.toString())

        apiService.publishComment(commentDTO)?.enqueue(object : Callback<RefDTO?> {

            override fun onResponse(call: Call<RefDTO?>, response: Response<RefDTO?>) {
                Log.e("publishComment", "succeeded")
                if (response.body() != null) {
                    callback.invoke(true, false)
                } else {
                    callback.invoke(false, true)
                }
            }

            override fun onFailure(call: Call<RefDTO?>, t: Throwable) {
                Log.e("publishComment", "failed")
                callback.invoke(false, false)
            }
        })

    }

    fun removeComment(commentREF: Int, callback: (success: Boolean, mode: Boolean) -> Unit) {
        apiService.removeComment(commentREF)?.enqueue(object : Callback<MessageDTO?> {
            override fun onResponse(call: Call<MessageDTO?>, response: Response<MessageDTO?>) {
                Log.i("Delete comment succeeded", "")
                if (response.body() != null) {
                    callback.invoke(true, true)
                } else {
                    callback.invoke(false, true)
                }
            }

            override fun onFailure(call: Call<MessageDTO?>, t: Throwable) {
                Log.i("Delete comment failed", "")
                callback.invoke(false, true)
            }
        })
    }
}