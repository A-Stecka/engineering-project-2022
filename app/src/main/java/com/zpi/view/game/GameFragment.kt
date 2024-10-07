package com.zpi.view.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zpi.viewmodel.MainViewModel
import com.zpi.R
import com.zpi.databinding.GameFragmentBinding
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.zpi.model.entity.GameStory
import com.zpi.viewmodel.GameViewModel

class GameFragment : Fragment() {

    private lateinit var viewModel: GameViewModel
    private var _binding: GameFragmentBinding? = null
    private val binding get() = _binding!!
    private var genreREF: Int? = null
    private var answer: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savdInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(activity!!)[GameViewModel::class.java]
        ViewModelProvider(activity!!)[MainViewModel::class.java].userRef?.observe(this, { ref ->
            viewModel.setUserRef(ref)
        })
        _binding = GameFragmentBinding.inflate(inflater, container, false)
        genreREF = arguments?.getInt("genreREF")
        viewModel.prepareStoryPair(genreREF!!, ::storyPairCallback)

        val pagerAdapter = GameStoryPagerAdapter(childFragmentManager, viewModel.aiStory, viewModel.userStory)
        val viewPager = binding.fragmentGameViewPager
        viewPager.adapter = pagerAdapter

        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                if (viewModel.aiStory.value != null) {
                    if (position == 0)
                        binding.fragmentGameAuthor.text = getString(R.string.author, viewModel.aiStory.value!!.author)
                    else
                        binding.fragmentGameAuthor.text = getString(R.string.story_generated)
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // pass
            }

            override fun onPageScrollStateChanged(state: Int) {
                // pass
            }
        })

        viewModel.aiStory.observe(viewLifecycleOwner) { story ->
            binding.apply {
                fragmentGameGenre.text = getString(R.string.genre, story!!.prompt.genre)
                fragmentGamePrompt.text = getString(R.string.prompt, story.prompt.getWordsString())
                fragmentGameAuthor.text = getString(R.string.author, story.author)
            }
        }

        binding.fragmentGameExitFrame.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_navigation_game)
        }

        binding.fragmentGameNextFrame.setOnClickListener {
            if (answer) {
                findNavController().navigate(
                    R.id.action_gameFragment_self,
                    bundleOf("genreREF" to genreREF)
                )
            } else {
                Toast.makeText(context, R.string.no_answer, Toast.LENGTH_SHORT).show()
            }
        }

        binding.fragmentGameChoiceAi.setOnClickListener {
            answer(0)
        }

        binding.fragmentGameChoiceHuman.setOnClickListener {
            answer(1)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_gameFragment_to_navigation_game)
            }
        })

        return binding.root
    }

    // choice = 0 -----> AI
    // choice = 1 -----> Human
    private fun answer(choice: Int) {
        answer = true
        binding.fragmentGameChoiceAi.isEnabled = false
        binding.fragmentGameChoiceHuman.isEnabled = false

        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_basic, null)
        val layoutTextView: TextView = dialogLayout.findViewById(R.id.textView)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        val titleTextView: TextView = dialogTitle.findViewById(R.id.textView)

        if (viewModel.aiStory.value!!.score == viewModel.userStory.value!!.score) {
            titleTextView.text = getString(R.string.answer_draw_label)
            layoutTextView.text = getString(R.string.answer_draw, viewModel.aiStory.value!!.score)
            viewModel.addMinigameResult(0.5, viewModel.aiStory.value!!.ref, viewModel.userREF!!, ::answerCallback)
        } else {
            if (choice == 0) {
                if (viewModel.aiStory.value!!.score > viewModel.userStory.value!!.score) {
                    titleTextView.text = getString(R.string.answer_correct_label)
                    layoutTextView.text = getString(R.string.answer_correct_ai,
                        viewModel.aiStory.value!!.score, viewModel.userStory.value!!.author, viewModel.userStory.value!!.score)
                    viewModel.addMinigameResult(1.0, viewModel.aiStory.value!!.ref, viewModel.userREF!!, ::answerCallback)
                } else {
                    titleTextView.text = getString(R.string.answer_wrong_label)
                    layoutTextView.text = getString(R.string.answer_wrong_ai,
                        viewModel.aiStory.value!!.score, viewModel.userStory.value!!.author, viewModel.userStory.value!!.score)
                    viewModel.addMinigameResult(0.0, viewModel.aiStory.value!!.ref, viewModel.userREF!!, ::answerCallback)
                }
            } else {
                if (viewModel.aiStory.value!!.score < viewModel.userStory.value!!.score) {
                    titleTextView.text = getString(R.string.answer_correct_label)
                    layoutTextView.text = getString(R.string.answer_correct_human,
                        viewModel.userStory.value!!.author, viewModel.userStory.value!!.score, viewModel.aiStory.value!!.score)
                    viewModel.addMinigameResult(1.0,  viewModel.aiStory.value!!.ref, viewModel.userREF!!, ::answerCallback)
                } else {
                    titleTextView.text = getString(R.string.answer_wrong_label)
                    layoutTextView.text = getString(R.string.answer_wrong_human,
                        viewModel.userStory.value!!.author, viewModel.userStory.value!!.score, viewModel.aiStory.value!!.score)
                    viewModel.addMinigameResult(0.0, viewModel.aiStory.value!!.ref, viewModel.userREF!!, ::answerCallback)
                }
            }
        }

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(getString(R.string.close)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val closeButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            closeButton.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun storyPairCallback(success: Boolean) {
        if (success) {
            binding.fragmentGameProgressBar.visibility = View.GONE
            binding.fragmentGameElements.visibility = View.VISIBLE
        } else {
            Toast.makeText(context, R.string.no_stories, Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_gameFragment_to_navigation_game)
        }
    }

    private fun answerCallback(success: Boolean) {
        if (!success)
            Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
    }

    inner class GameStoryPagerAdapter(fm: FragmentManager,
                                      private val aiStory: MutableLiveData<GameStory>,
                                      private val userStory: MutableLiveData<GameStory>) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = 2

        override fun getItem(i: Int): Fragment {
            if (i == 0) {
                return GameStoryFragment(this.userStory)
            }
            return GameStoryFragment(this.aiStory)
        }

        override fun getPageTitle(position: Int): CharSequence {
            if (position == 0)
                return getString(R.string.human)
            return getString(R.string.ai)
        }
    }

}