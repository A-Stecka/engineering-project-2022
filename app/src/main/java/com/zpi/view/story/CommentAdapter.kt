package com.zpi.view.story

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zpi.R
import com.zpi.model.entity.Comment

class CommentAdapter(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val userRef: Int,
    private val onCommentSelectedListener: OnCommentSelectedListener? = null
) : ListAdapter<Comment, CommentAdapter.CommentViewHolder>(CommentItemDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class CommentViewHolder(iv: View) : RecyclerView.ViewHolder(iv) {

        private val pictureImage: AppCompatImageView = iv.findViewById(R.id.profile_comment_image)
        private val deleteFrame: ConstraintLayout = iv.findViewById(R.id.delete_frame)
        private val usernameText: TextView = iv.findViewById(R.id.username_text)
        private val contentView: TextView = iv.findViewById(R.id.comment_text)

        fun bindTo(comment: Comment) {
            val profilePictureArray = context.resources.obtainTypedArray(R.array.profile_pictures)
            pictureImage.setImageResource(profilePictureArray.getResourceId(comment.profilePicture - 1, 0))
            profilePictureArray.recycle()
            usernameText.text = comment.username
            contentView.text = comment.content
            if (comment.fkUser == userRef) {
                deleteFrame.visibility = View.VISIBLE
                deleteFrame.setOnClickListener {
                    showConfirmDialog(comment)
                }
            }
        }

        private fun showConfirmDialog(comment: Comment) {
            val builder = AlertDialog.Builder(context)
            val inflater: LayoutInflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.dialog_basic, null)
            dialogLayout.findViewById<TextView>(R.id.textView).text = context.getString(R.string.confirm_cannot_undo)
            val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
            dialogTitle.findViewById<TextView>(R.id.textView).text = context.getString(R.string.confirm_remove_comment)

            with(builder) {
                setCustomTitle(dialogTitle)
                setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                    onCommentSelectedListener!!.deleteComment(comment)
                }
                setNegativeButton(context.getString(R.string.no)) { _, _ -> }
                setView(dialogLayout)
                show()
            }
        }
    }
}
