package com.zpi.view.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zpi.R
import com.zpi.viewmodel.MainViewModel
import com.zpi.databinding.FragmentHomeBinding
import com.zpi.model.entity.Story
import com.zpi.view.search.QueryHandler
import com.zpi.view.shared.OnStorySelectedListener
import com.zpi.view.shared.StoryListItemAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import com.zpi.view.shared.HelpDialog
import com.zpi.viewmodel.HomeViewModel

class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var buttonLowest: FloatingActionButton? = null
    private var buttonMiddle: FloatingActionButton? = null
    private var buttonHighest: FloatingActionButton? = null
    private var floatingVisibility: Boolean = false

    private var buttonLowestValue: Int = 1
    private var buttonMiddleValue: Int = 3
    private var buttonHighestValue: Int = 5

    private var firstLoad: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        ViewModelProvider(requireActivity())[MainViewModel::class.java].userRef?.observe(viewLifecycleOwner) { ref ->
            homeViewModel.setUserRef(ref)
        }
        ViewModelProvider(requireActivity())[MainViewModel::class.java].firstLogin?.observe(viewLifecycleOwner) { value ->
            if (value == 0) {
                HelpDialog(requireContext(), layoutInflater).show()
                ViewModelProvider(requireActivity())[MainViewModel::class.java].setFirstLogin(1)
            }
        }

        buttonHighest = binding.promptButtonHighest
        buttonMiddle = binding.promptButtonMiddle
        buttonLowest = binding.promptButtonLowest
        floatingVisibility = false

        changeOptionsVisibility(floatingVisibility)

        binding.mainPromptButton.setOnClickListener {
            floatingVisibility = !floatingVisibility
            changeOptionsVisibility(floatingVisibility)
        }

        binding.promptButtonHighest.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_writeFragment,
                bundleOf("noOfWords" to buttonHighestValue))
        }

        binding.promptButtonMiddle.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_writeFragment,
                bundleOf("noOfWords" to buttonMiddleValue))
        }

        binding.promptButtonLowest.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_home_to_writeFragment,
                bundleOf("noOfWords" to buttonLowestValue))
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(context, getString(R.string.log_out_info), Toast.LENGTH_LONG).show()
            }
        })

        val adapter = StoryListItemAdapter(object : OnStorySelectedListener {
            override fun showFullStory(story: Story) {
                findNavController().navigate(R.id.action_navigation_home_to_readYourStoryFragment,
                    bundleOf("story" to story))
            }
        })
        binding.shortStoryItemList.adapter = adapter
        homeViewModel.stories.observe(viewLifecycleOwner) { storiesChanged ->
            if (storiesChanged.isNotEmpty() && storiesChanged[0].ref == -1){
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_connection),
                    Toast.LENGTH_LONG
                ).show()
            } else if (storiesChanged.isEmpty() && firstLoad) {
                binding.noStoriesLabel.visibility = View.VISIBLE
            } else {
                binding.noStoriesLabel.visibility = View.GONE
                adapter.submitList(storiesChanged)
            }
            firstLoad = true
        }

        val id: Int = binding.searchWidget.context.resources.getIdentifier("android:id/search_src_text", null, null)
        val searchText: TextView = binding.searchWidget.findViewById(id)
        searchText.typeface = ResourcesCompat.getFont(requireContext(), R.font.alata)

        QueryHandler(requireContext(), requireActivity(), binding.searchWidget)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeOptionsVisibility(destinedVisibility: Boolean) {
        if (destinedVisibility) {
            buttonHighest?.visibility = View.VISIBLE
            buttonMiddle?.visibility = View.VISIBLE
            buttonLowest?.visibility = View.VISIBLE
        } else {
            buttonHighest?.visibility = View.GONE
            buttonMiddle?.visibility = View.GONE
            buttonLowest?.visibility = View.GONE
        }
    }

}