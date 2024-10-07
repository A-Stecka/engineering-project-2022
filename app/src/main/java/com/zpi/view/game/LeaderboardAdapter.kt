package com.zpi.view.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zpi.R
import com.zpi.model.entity.LeaderboardItem

class LeaderboardAdapter :
    ListAdapter<LeaderboardItem, LeaderboardAdapter.LeaderboardItemHolder>(LeaderboardItemDiff()) {

    private val maxPositions: Int = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardAdapter.LeaderboardItemHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.leader_list_item, parent, false)
        return LeaderboardItemHolder(itemView)
    }

    override fun onBindViewHolder(holder: LeaderboardItemHolder, position: Int) {
        if (position <= maxPositions) {
            holder.bindTo(getItem(position), position)
        }
    }

    inner class LeaderboardItemHolder(iv: View) : RecyclerView.ViewHolder(iv) {

        private val position: TextView = iv.findViewById(R.id.leader_list_item_position)
        private val name: TextView = iv.findViewById(R.id.leader_list_item_name)
        private val score: TextView = iv.findViewById(R.id.leader_list_item_score)

        fun bindTo(leader: LeaderboardItem, positionInt: Int) {
            position.text = (positionInt + 1).toString()
            name.text = leader.name
            score.text = leader.minigameScore.toString()
        }
    }

    class LeaderboardItemDiff : DiffUtil.ItemCallback<LeaderboardItem>(){
        override fun areItemsTheSame(oldItem: LeaderboardItem, newItem: LeaderboardItem): Boolean {
            return oldItem.name == newItem.name && oldItem.minigameScore == newItem.minigameScore
        }

        override fun areContentsTheSame(oldItem: LeaderboardItem, newItem: LeaderboardItem): Boolean {
            return oldItem.name == newItem.name && oldItem.minigameScore == newItem.minigameScore
        }

    }
}
