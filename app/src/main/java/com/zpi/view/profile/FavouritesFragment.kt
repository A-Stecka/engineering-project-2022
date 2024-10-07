package com.zpi.view.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zpi.viewmodel.MainViewModel
import com.zpi.R
import com.zpi.databinding.FragmentFavouritesBinding
import com.zpi.model.entity.Story
import com.zpi.view.shared.OnStorySelectedListener
import com.zpi.view.shared.StoryListItemAdapter
import com.zpi.viewmodel.FavouritesViewModel

class FavouritesFragment : Fragment() {

    private lateinit var viewModel: FavouritesViewModel
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this)[FavouritesViewModel::class.java]
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)

        ViewModelProvider(requireActivity())[MainViewModel::class.java].userRef?.observe(viewLifecycleOwner) { ref ->
            viewModel.setUserRef(ref)
        }

        val adapter = StoryListItemAdapter(object : OnStorySelectedListener {
            override fun showFullStory(story: Story) {
                findNavController().navigate(R.id.action_favouritesFragment_to_readOtherStoryFragment,
                    bundleOf("story" to story, "userRef" to viewModel.userREF!!, "launchMode" to 1))
            }
        })
        binding.shortStoryItemListFavourites.adapter = adapter
        viewModel.favourites.observe(viewLifecycleOwner) { storiesChanged ->
            if (storiesChanged.isNotEmpty() && storiesChanged[0].ref == -1) {
                Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            } else {
                adapter.submitList(storiesChanged)
            }
        }

        binding.fragmentFavouritesBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_favouritesFragment_to_navigation_profile)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_favouritesFragment_to_navigation_profile)
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}