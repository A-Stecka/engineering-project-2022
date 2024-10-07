package com.zpi.view.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.zpi.databinding.FragmentGameStoryBinding
import com.zpi.model.entity.GameStory
import com.zpi.viewmodel.GameViewModel


class GameStoryFragment(private val story: MutableLiveData<GameStory>) : Fragment() {

    private lateinit var viewModel: GameViewModel
    private var _binding: FragmentGameStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        _binding = FragmentGameStoryBinding.inflate(inflater, container, false)

        story.observe(this, {
            binding.fragmentGameStoryContent.text = it.content
        })

        return binding.root
    }

}