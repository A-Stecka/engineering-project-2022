package com.zpi.view.profile

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.zpi.R

class GridAdapter(c: Context): BaseAdapter(){
    private val context: Context = c

    private val images = context.resources.obtainTypedArray(R.array.profile_pictures)

    override fun getCount(): Int {
        return images.length()
    }

    override fun getItem(position: Int): Any {
        return images.getResourceId(position, 0)
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val imageView: AppCompatImageView
        if (view == null) {
            imageView = AppCompatImageView(context)
            imageView.layoutParams = AbsListView.LayoutParams(200, 200)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else imageView = view as AppCompatImageView
        imageView.setImageResource(images.getResourceId(position, 0))
        return imageView
    }

    fun getItemInt(position: Int): Int {
        return images.getResourceId(position, 0)
    }
}
