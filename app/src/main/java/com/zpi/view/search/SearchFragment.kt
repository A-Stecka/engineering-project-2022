package com.zpi.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zpi.R
import com.zpi.databinding.FragmentSearchBinding
import com.zpi.model.entity.Story
import com.zpi.view.shared.OnStorySelectedListener
import com.zpi.view.shared.StoryListItemAdapter
import com.zpi.viewmodel.SearchViewModel

class SearchFragment : Fragment() {
    private lateinit var viewModel: SearchViewModel
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        val adapter = StoryListItemAdapter(object : OnStorySelectedListener {
            override fun showFullStory(story: Story) {
                findNavController().navigate(
                    R.id.action_searchFragment_to_readOtherStoryFragmentSearch,
                    bundleOf("story" to story, "userRef" to story.fkUser, "launchMode" to 2)
                )
            }
        })

        binding.activitySearchBackButton.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.shortStoryItemListSearch.adapter = adapter

        viewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        viewModel.query.observe(viewLifecycleOwner) { t ->
            run {
                binding.activitySearchSearchView.setQuery(t, false)
            }
        }
        viewModel.stories.observe(viewLifecycleOwner) { storiesChanged ->
            if (storiesChanged.isNotEmpty()) {
                adapter.submitList(storiesChanged)
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            }
        }

        QueryHandler(requireActivity().applicationContext, requireActivity(), binding.activitySearchSearchView, true)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}