package com.zpi.view.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.zpi.viewmodel.MainViewModel
import com.zpi.R
import com.zpi.databinding.NavigationGameBinding
import com.zpi.model.entity.Genre
import com.zpi.viewmodel.GameViewModel

class GameMainFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private var _binding: NavigationGameBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(activity!!)[GameViewModel::class.java]
        _binding = NavigationGameBinding.inflate(inflater, container, false)
        viewModel.getLeaderboard()
        viewModel.getGenres()

        ViewModelProvider(activity!!)[MainViewModel::class.java].userRef?.observe(this, { ref ->
            viewModel.setUserRef(ref)
        })

        val adapter = LeaderboardAdapter()
        binding.leaderTableList.adapter = adapter
        viewModel.leaders.observe(this, { leadersChanged -> adapter.submitList(leadersChanged) })

        viewModel.genres.observe(this, { genres: List<Genre> ->
            binding.categoriesGame.children.iterator().forEachRemaining { view -> view.visibility = View.GONE }
            var columnGenres = 0
            var row = TableRow(context)
            genres.forEach { genre ->
                columnGenres += 1
                val genreButton = Chip(context)

                val params = TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(15, 0, 15, 0)
                genreButton.layoutParams = params

                genreButton.typeface = ResourcesCompat.getFont(context!!, R.font.alata)
                genreButton.setChipBackgroundColorResource(R.color.chip_button_clickable)
                genreButton.setTextColor(resources.getColor(R.color.text, null))
                genreButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
                genreButton.text = genre.value

                genreButton.setOnClickListener {
                    findNavController().navigate(
                        R.id.action_navigation_game_to_gameFragment,
                        bundleOf("genreREF" to genre.ref)
                    )
                }
                row.addView(genreButton)
                if (columnGenres % 3 == 0) {
                    binding.categoriesGame.addView(row)
                    row = TableRow(context)
                    columnGenres = 0
                }
            }
            binding.categoriesGame.addView(row)
        })

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // pass
            }
        })

        return binding.root
    }

}