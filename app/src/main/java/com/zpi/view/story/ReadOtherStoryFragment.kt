package com.zpi.view.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.zpi.model.entity.Comment
import com.zpi.model.entity.Story
import com.zpi.view.shared.InfoDialog
import com.zpi.view.shared.Validator
import com.zpi.viewmodel.ReadStoryViewModel

class ReadOtherStoryFragment : Fragment() {

    private lateinit var readStoryViewModel: ReadStoryViewModel
    private var _binding: FragmentReadStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var commentAdapter: CommentAdapter
    private var story: Story? = null
    private var userRef: Int? = null
    private var isScoreInBaseOnStart = false
    private lateinit var validator: Validator

    // launchMode = 0 ----> community
    // launchMode = 1 ----> favourites
    // launchMode = 2 ----> search
    private var launchMode: Int? = null
    private var imageId = -10
    private var rating = 0f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        story = arguments?.getParcelable("story")
        userRef = arguments?.getInt("userRef")
        launchMode = arguments?.getInt("launchMode")

        _binding = FragmentReadStoryBinding.inflate(inflater, container, false)
        readStoryViewModel = ViewModelProvider(this)[ReadStoryViewModel::class.java]

        ViewModelProvider(requireActivity())[MainViewModel::class.java].userRef?.observe(viewLifecycleOwner) { ref ->
            readStoryViewModel.setUserRef(ref)
        }

        readStoryViewModel.setStoryREF(story!!.ref)

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

        readStoryViewModel.getGeneratedStory()
        readStoryViewModel.getComments()
        readStoryViewModel.getBannedWords()
        readStoryViewModel.getFavRef(userRef!!)
        readStoryViewModel.getScoreValue(story!!.ref, userRef!!)
        readStoryViewModel.getAverageScore(story!!.ref)

        readStoryViewModel.bannedWords.observe(viewLifecycleOwner) { bannedWords ->
            if (bannedWords.isNotEmpty() && bannedWords[0].value != "")
                validator = Validator(requireContext(), bannedWords)
        }

        readStoryViewModel.favouriteREF.observe(viewLifecycleOwner) { favRefChanged ->
            when (favRefChanged) {
                -10 -> {
                    Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
                }
                -1 -> {
                    binding.fragmentReadActionIcon.setImageResource(R.drawable.icon_transparent)
                    binding.fragmentReadActionLabel.text = ""
                }
                0 -> {
                    binding.fragmentReadActionIcon.setImageResource(R.drawable.icon_favourites)
                    binding.fragmentReadActionLabel.text = getString(R.string.add_fav_label)
                }
                else -> {
                    binding.fragmentReadActionIcon.setImageResource(R.drawable.icon_favourites_full)
                    binding.fragmentReadActionLabel.text = getString(R.string.remove_fav_label)
                }
            }

            imageId = when (favRefChanged) {
                -1 -> R.drawable.icon_transparent
                0 -> R.drawable.icon_favourites
                else -> R.drawable.icon_favourites_full
            }

            binding.fragmentReadActionFrame.setOnClickListener {
                when (imageId) {
                    R.drawable.icon_favourites -> readStoryViewModel.addFavourite(userRef!!, story!!.ref)
                    R.drawable.icon_favourites_full -> readStoryViewModel.removeFavourite(favRefChanged)
                }
            }
        }

        readStoryViewModel.scoreValue.observe(viewLifecycleOwner) { newValue ->
            if (newValue != -10f) {
                if (readStoryViewModel.scoreRef.value!! > 0) {
                    readStoryViewModel.getAverageScore(story!!.ref)
                    isScoreInBaseOnStart = true
                    binding.fragmentReadRatingBar.rating = newValue
                    isScoreInBaseOnStart = false
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            }
        }

        readStoryViewModel.avgScore.observe(viewLifecycleOwner) { newValue ->
            when (newValue) {
                -10.0f -> binding.fragmentReadAvgScore.text = ""
                -1.0f -> binding.fragmentReadAvgScore.text = getString(R.string.avg_score_none)
                -100f -> Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
                else -> binding.fragmentReadAvgScore.text = getString(R.string.avg_score, newValue.toString())
            }
        }

        readStoryViewModel.generatedStory.observe(viewLifecycleOwner) { aiStory ->
            if (aiStory != null && aiStory.content != "") {
                binding.fragmentReadGenerated.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                binding.fragmentReadGenerated.text = aiStory.content
            }
        }

        binding.fragmentReadRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            this.rating = rating
            if (!isScoreInBaseOnStart) {
                readStoryViewModel.addScore(story!!.ref, userRef!!, rating)
            }
        }

        commentAdapter = CommentAdapter(requireContext(), layoutInflater, userRef!!, object : OnCommentSelectedListener {
            override fun deleteComment(comment: Comment) {
                readStoryViewModel.removeComment(comment.REF, ::commentCallback)
            }
        })

        binding.fragmentReadCommentsRecycler.adapter = commentAdapter
        readStoryViewModel.comments.observe(viewLifecycleOwner) { commentsChanged ->
            if (commentsChanged.isNotEmpty() && commentsChanged[0].REF == -1) {
                Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            } else {
                commentAdapter.submitList(commentsChanged)
            }
        }

        binding.fragmentReadBackButton.setOnClickListener {
            when (launchMode) {
                0 -> findNavController().navigate(R.id.action_readOtherStoryFragment_to_navigation_community)
                1 -> findNavController().navigate(R.id.action_readOtherStoryFragment_to_favouritesFragment)
                2 -> findNavController().navigate(R.id.action_readOtherStoryFragmentSearch_to_searchFragment)
            }
        }

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

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (launchMode) {
                    0 -> findNavController().navigate(R.id.action_readOtherStoryFragment_to_navigation_community)
                    1 -> findNavController().navigate(R.id.action_readOtherStoryFragment_to_favouritesFragment)
                    2 -> findNavController().navigate(R.id.action_readOtherStoryFragmentSearch_to_searchFragment)
                }
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            fragmentReadTitle.text = story!!.title
            fragmentReadGenre.text = getString(R.string.genre, story!!.prompt.genre)
            fragmentReadPrompt.text = getString(R.string.prompt, story!!.prompt.getWordsString())
            fragmentReadAuthor.visibility = View.VISIBLE
            fragmentReadAuthor.text = getString(R.string.author, story!!.author)
            fragmentReadStory.text = story!!.content
            fragmentReadRatingBar.isEnabled = true
            fragmentReadAddCommentButton.visibility = View.VISIBLE
        }

        binding.fragmentReadAddCommentButton.setOnClickListener {
            showAddCommentDialog()
        }
    }

    private fun showAddCommentDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_comment, null)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        dialogTitle.findViewById<TextView>(R.id.textView).text = getString(R.string.add_comment)

        val editText = dialogLayout.findViewById<EditText>(R.id.dialog_comment_input)

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(getString(R.string.add)) { _, _ -> }
            setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            addButton.setOnClickListener {
                if (validator.ifValidComment(editText.text.toString())) {
                    readStoryViewModel.publishComment(editText.text.toString(), ::commentCallback)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun commentCallback(success: Boolean, mode: Boolean) {
        if (success) {
            if (mode)
                Toast.makeText(context, getString(R.string.success_comment_removed), Toast.LENGTH_LONG).show()
            readStoryViewModel.getComments()
        } else
            Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
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
