package com.zpi.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zpi.viewmodel.MainViewModel
import com.zpi.R
import com.zpi.databinding.FragmentStatsBinding
import com.zpi.viewmodel.StatsViewModel
import kotlin.math.round

class StatsFragment : Fragment() {
    private lateinit var statsViewModel: StatsViewModel
    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)

        statsViewModel = ViewModelProvider(this)[StatsViewModel::class.java]
        ViewModelProvider(requireActivity())[MainViewModel::class.java].userRef?.observe(viewLifecycleOwner) { ref ->
            statsViewModel.setUserRef(ref)
            statsViewModel.getUserStatistics()
            statsViewModel.getUserStatisticsPerGenre()
        }

        statsViewModel.stats.observe(viewLifecycleOwner) { changedStats ->
            if (changedStats.stories != -10) {
                if (changedStats.stories != -1) {
                    binding.fragmentStatsStories.text = changedStats.stories.toString()
                    binding.fragmentStatsComments.text = changedStats.comments.toString()
                    binding.fragmentStatsCommentsByOther.text = changedStats.commentsOthers.toString()
                    binding.fragmentStatsFavourites.text = changedStats.favourites.toString()
                    binding.fragmentStatsFavouritesByOther.text =
                        if (changedStats.favouritesOthers == 1)
                            getString(R.string.stats_time, changedStats.favouritesOthers.toString())
                        else
                            getString(R.string.stats_times, changedStats.favouritesOthers.toString())
                    binding.fragmentStatsRated.text =
                        if (changedStats.ratedOthers == 1)
                            getString(R.string.stats_time, changedStats.ratedOthers.toString())
                        else
                            getString(R.string.stats_times, changedStats.ratedOthers.toString())
                    binding.fragmentStatsAvgWords.text = changedStats.avgWords.toString()
                    binding.fragmentStatsWordsCount.text = changedStats.wordsCount.toString()
                    binding.fragmentStatsStreak.text =
                        if (changedStats.streak == 1)
                            getString(R.string.stats_day, changedStats.streak.toString())
                        else
                            getString(R.string.stats_days, changedStats.streak.toString())
                    binding.fragmentStatsMinigame.text = changedStats.minigameScore.toString()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            }
        }

        val adapter = StoriesPerGenreAdapter()
        binding.storiesPerGenreList.adapter = adapter
        statsViewModel.storiesPerGenre.observe(viewLifecycleOwner) { storiesChanged ->
            if (storiesChanged.isNotEmpty() && storiesChanged[0].genre == "") {
                Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            } else {
                adapter.submitList(storiesChanged)
            }
        }

        binding.fragmentStatsBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_statsFragment_to_navigation_profile)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_statsFragment_to_navigation_profile)
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}