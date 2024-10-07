package com.zpi.view.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zpi.viewmodel.MainViewModel
import com.zpi.R
import com.zpi.databinding.FragmentReadStoryBinding
import com.zpi.model.entity.Story
import com.zpi.view.shared.InfoDialog
import com.zpi.viewmodel.ReadStoryViewModel

class ReadYourStoryFragment : Fragment() {

    private lateinit var readStoryViewModel: ReadStoryViewModel

    private var _binding: FragmentReadStoryBinding? = null
    private val binding get() = _binding!!
    private var story: Story? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        story = arguments?.getParcelable("story")

        _binding = FragmentReadStoryBinding.inflate(inflater, container, false)
        readStoryViewModel = ViewModelProvider(this)[ReadStoryViewModel::class.java]

        ViewModelProvider(requireActivity())[MainViewModel::class.java].userRef?.observe(viewLifecycleOwner) { ref ->
            readStoryViewModel.setUserRef(ref)
        }

        readStoryViewModel.setStoryREF(story!!.ref)
        readStoryViewModel.getGeneratedStory()
        readStoryViewModel.getComments()

        binding.fragmentReadActionFrame.setOnClickListener {
            showConfirmDialog()
        }

        val commentAdapter = CommentAdapter(requireContext(), layoutInflater, story!!.fkUser)
        binding.fragmentReadCommentsRecycler.adapter = commentAdapter
        readStoryViewModel.comments.observe(viewLifecycleOwner) { commentsChanged ->
            if (commentsChanged.isNotEmpty() && commentsChanged[0].REF == -1) {
                Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            } else {
                commentAdapter.submitList(commentsChanged)
            }
        }

        readStoryViewModel.getAverageScore(story!!.ref)

        readStoryViewModel.avgScore.observe(viewLifecycleOwner) { scoreChanged ->
            when (scoreChanged) {
                -10.0f -> binding.fragmentReadAvgScore.text = ""
                -1.0f -> binding.fragmentReadAvgScore.text = getString(R.string.avg_score_your)
                -100.0f ->
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_connection),
                        Toast.LENGTH_LONG)
                    .show()
                else -> {
                    binding.apply {
                        fragmentReadAvgScore.text = getString(R.string.avg_score, scoreChanged.toString())
                        fragmentReadRatingBar.rating = scoreChanged
                    }
                }
            }
        }

        readStoryViewModel.generatedStory.observe(viewLifecycleOwner) { aiStory ->
            if (aiStory != null && aiStory.content != "") {
                binding.fragmentReadGenerated.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                binding.fragmentReadGenerated.text = aiStory.content
            }
        }

        readStoryViewModel.storyAnalysis.observe(this, { analysis ->
            if (analysis == null)
                binding.apply {
                    fragmentReadAiElements.visibility = View.GONE
                    fragmentReadAiContentNotReady.visibility = View.VISIBLE
                }
            else
                binding.apply {
                    fragmentReadAiElements.visibility = View.VISIBLE
                    fragmentReadAiContentNotReady.visibility = View.GONE
                    fragmentReadAiGenre.text = analysis.genre ?: getString(R.string.ai_analysis_element_not_ready)
                    fragmentReadAiCompatibility.text = changeToPercent(analysis.genrePoints) ?: getString(R.string.ai_analysis_element_not_ready)
                    fragmentReadAiCompletion.text = changeToPercent(analysis.promptCompletionPoints) ?: getString(R.string.ai_analysis_element_not_ready)
                    fragmentReadAiCorrectness.text = changeToPercent(analysis.correctnessPoints) ?: getString(R.string.ai_analysis_element_not_ready)
                    fragmentReadAiVariety.text = changeToPercent(analysis.vocabularyVarietyPoints) ?: getString(R.string.ai_analysis_element_not_ready)
                    fragmentReadAiPositiveness.text = analysis.positivenessScore ?: getString(R.string.ai_analysis_element_not_ready)
                    fragmentReadAiNegativeness.text = analysis.negativenessScore ?: getString(R.string.ai_analysis_element_not_ready)
                    fragmentReadAiNeutralness.text = analysis.neutralnessScore ?: getString(R.string.ai_analysis_element_not_ready)
                }
        })

        binding.fragmentReadActionStoryFrame.setOnClickListener {
            if (binding.fragmentReadActionStoryLabel.text == getString(R.string.hide)) {
                binding.fragmentReadStory.visibility = View.GONE
                binding.fragmentReadActionStoryLabel.text = getString(R.string.show)
                binding.fragmentReadActionStoryIcon.contentDescription = getString(R.string.show)
                binding.fragmentReadActionStoryIcon.setImageResource(R.drawable.icon_more)
            } else {
                binding.fragmentReadStory.visibility = View.VISIBLE
                binding.fragmentReadActionStoryLabel.text = getString(R.string.hide)
                binding.fragmentReadActionStoryIcon.contentDescription = getString(R.string.hide)
                binding.fragmentReadActionStoryIcon.setImageResource(R.drawable.icon_less)
            }
        }

        binding.fragmentReadActionGeneratedFrame.setOnClickListener {
            if (binding.fragmentReadActionGeneratedLabel.text == getString(R.string.hide)) {
                binding.fragmentReadGenerated.visibility = View.GONE
                binding.fragmentReadActionGeneratedLabel.text = getString(R.string.show)
                binding.fragmentReadActionGeneratedIcon.contentDescription = getString(R.string.show)
                binding.fragmentReadActionGeneratedIcon.setImageResource(R.drawable.icon_more)
            } else {
                binding.fragmentReadGenerated.visibility = View.VISIBLE
                binding.fragmentReadActionGeneratedLabel.text = getString(R.string.hide)
                binding.fragmentReadActionGeneratedIcon.contentDescription = getString(R.string.hide)
                binding.fragmentReadActionGeneratedIcon.setImageResource(R.drawable.icon_less)
            }
        }

        binding.fragmentReadActionInfoFrame.setOnClickListener {
            InfoDialog(requireContext(), layoutInflater).show()
        }

        binding.fragmentReadBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_readYourStoryFragment_to_navigation_home)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_readYourStoryFragment_to_navigation_home)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            fragmentReadActionIcon.setImageResource(R.drawable.icon_delete)
            fragmentReadActionLabel.text = getString(R.string.delete_label)
            fragmentReadTitle.text = story!!.title
            fragmentReadGenre.text = getString(R.string.genre, story!!.prompt.genre)
            fragmentReadPrompt.text = getString(R.string.prompt, story!!.prompt.getWordsString())
            fragmentReadAuthor.visibility = View.GONE
            fragmentReadStory.text = story!!.content
            fragmentReadRatingBar.visibility = View.GONE
            fragmentReadAddCommentButton.visibility = View.GONE
        }
    }

    private fun removeCallback(success: Boolean) {
        if (success) {
            Toast.makeText(context, getString(R.string.success_story_removed), Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_readYourStoryFragment_to_navigation_home)
        } else
            Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_basic, null)
        dialogLayout.findViewById<TextView>(R.id.textView).text = getString(R.string.confirm_cannot_undo)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        dialogTitle.findViewById<TextView>(R.id.textView).text = getString(R.string.confirm_remove_story)

        with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                readStoryViewModel.removeStory(story!!.ref, ::removeCallback)
            }
            setNegativeButton(getString(R.string.no)) { _, _ -> }
            setView(dialogLayout)
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun changeToPercent(value: Double?): String? {
        if (value != null) {
            val result = value * 100
            return result.toInt().toString() + "%"
        }
        return value
    }

}
