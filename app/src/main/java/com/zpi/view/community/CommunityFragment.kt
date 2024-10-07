package com.zpi.view.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
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
import com.zpi.databinding.FragmentCommunityBinding
import com.zpi.model.entity.Genre
import com.zpi.model.entity.Story
import com.zpi.view.search.QueryHandler
import com.zpi.view.shared.OnStorySelectedListener
import com.zpi.view.shared.StoryListItemAdapter
import com.zpi.viewmodel.CommunityViewModel

class CommunityFragment : Fragment() {

    private lateinit var viewModel: CommunityViewModel
    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private var firstLoad: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this)[CommunityViewModel::class.java]
        viewModel.init()
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        ViewModelProvider(requireActivity())[MainViewModel::class.java].userRef?.observe(viewLifecycleOwner) { ref ->
            viewModel.setUserRef(ref)
        }

        val storyAdapter = StoryListItemAdapter(object : OnStorySelectedListener {
            override fun showFullStory(story: Story) {
                findNavController().navigate(R.id.action_navigation_community_to_readOtherStoryFragment,
                    bundleOf("story" to story, "userRef" to viewModel.userREF!!, "launchMode" to 0)
                )
            }
        })

        viewModel.getGenres().observe(viewLifecycleOwner) { genres: List<Genre> ->
            if (genres.isNotEmpty() && genres[0].ref == -1) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_connection),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                binding.categoriesCommunity.children.iterator()
                    .forEachRemaining { view -> view.visibility = View.GONE }
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

                    genreButton.typeface = ResourcesCompat.getFont(requireContext(), R.font.alata)
                    genreButton.setChipBackgroundColorResource(R.color.chip_button_selectable)
                    genreButton.setTextColor(resources.getColor(R.color.text, null))
                    genreButton.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    genreButton.text = genre.value
                    genreButton.isCheckable = true
                    genreButton.isChecked = true
                    genreButton.isCheckedIconVisible = false
                    genreButton.setOnCheckedChangeListener { _, bool ->
                        viewModel.changeGenre(genre)
                        genreButton.isChecked = bool
                    }
                    row.addView(genreButton)
                    if (columnGenres % 3 == 0) {
                        binding.categoriesCommunity.addView(row)
                        row = TableRow(context)
                        columnGenres = 0
                    }
                }
                binding.categoriesCommunity.addView(row)
            }
        }

        binding.filterFrame.setOnClickListener {
            if (binding.categoriesCommunity.visibility == View.VISIBLE) {
                binding.categoriesCommunity.visibility = View.GONE
                binding.filterButton.contentDescription = getString(R.string.filter_label)
                binding.filterLabel.text = getString(R.string.filter_label)
                binding.filterButton.setImageResource(R.drawable.icon_filter)
            } else {
                binding.categoriesCommunity.visibility = View.VISIBLE
                binding.filterButton.contentDescription = getString(R.string.close)
                binding.filterLabel.text = getString(R.string.close)
                binding.filterButton.setImageResource(R.drawable.icon_close)
            }
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // pass
            }
        })

        binding.shortStoryItemListCommunity.adapter = storyAdapter
        viewModel.stories.observe(viewLifecycleOwner) { storiesChanged ->
            if (storiesChanged.isNotEmpty() && storiesChanged[0].ref == -1) {
                Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            }
            else if (storiesChanged.isEmpty() && firstLoad) {
                binding.noStoriesLabel.visibility = View.VISIBLE
            }
            else {
                binding.noStoriesLabel.visibility = View.GONE
                storyAdapter.submitList(storiesChanged)
            }
            firstLoad = true
        }

        val id: Int = binding.searchWidgetCommunity.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val searchText: TextView = binding.searchWidgetCommunity.findViewById(id)
        searchText.typeface = ResourcesCompat.getFont(requireContext(), R.font.alata)

        QueryHandler(requireContext(), requireActivity(), binding.searchWidgetCommunity)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}