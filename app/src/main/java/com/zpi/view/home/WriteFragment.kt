package com.zpi.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.zpi.viewmodel.MainViewModel
import com.zpi.R
import com.zpi.databinding.FragmentWriteBinding
import com.zpi.view.shared.Validator
import com.zpi.viewmodel.WriteViewModel

class WriteFragment : Fragment() {
    private var _binding: FragmentWriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var writeViewModel: WriteViewModel
    private var noOfWords: Int? = null
    private lateinit var validator: Validator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        noOfWords = arguments?.getInt("noOfWords")
        _binding = FragmentWriteBinding.inflate(inflater, container, false)

        writeViewModel = ViewModelProvider(this)[WriteViewModel::class.java]
        ViewModelProvider(requireActivity())[MainViewModel::class.java].userRef?.observe(viewLifecycleOwner) { ref ->
            writeViewModel.setUserRef(ref)
        }

        writeViewModel.prompt.observe(viewLifecycleOwner) { changedPrompt ->
            if (changedPrompt.ref != -1 && changedPrompt.ref != -10) {
                binding.fragmentWriteGenre.text = changedPrompt.genre
                binding.fragmentWritePrompt.text = changedPrompt.words.joinToString(", ")
            } else {
                if (changedPrompt.ref != -10)
                    Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            }
        }

        writeViewModel.bannedWords.observe(viewLifecycleOwner) { bannedWords ->
            if (bannedWords.isNotEmpty() && bannedWords[0].value != ""){
                validator = Validator(requireContext(), bannedWords)
                binding.fragmentWritePublishButton.isEnabled = true
            }
        }

        writeViewModel.generateChallenge(noOfWords!!)
        writeViewModel.getBannedWords()

        binding.fragmentWritePublishButton.setOnClickListener {
            if (validate()) {
                binding.fragmentWriteProgressBar.visibility = View.VISIBLE
                binding.fragmentWriteElements.visibility = View.GONE
                writeViewModel.publishStory(binding.fragmentWriteInputTitle.text.toString(),
                    binding.fragmentWriteInputContent.text.toString(), ::publishStoryCallback)
            }
        }

        binding.fragmentWriteBackButton.setOnClickListener {
            showConfirmDialog()
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showConfirmDialog()
            }
        })

        return binding.root
    }

    private fun validate(): Boolean {
        if (binding.fragmentWriteInputContent.text.toString() == "" || binding.fragmentWriteInputTitle.text.toString() == "") {
            Toast.makeText(context, getString(R.string.error_empty, "Title and content fields"), Toast.LENGTH_LONG).show()
            return false
        }
        val validTitle = validator.ifValidTitle(binding.fragmentWriteInputTitle.text.toString())
        if (validTitle)
            return validator.ifValidStory(binding.fragmentWriteInputContent.text.toString())
        return false
    }

    private fun publishStoryCallback(success: Boolean) {
        if (success)
            findNavController().navigate(R.id.action_writeFragment_to_navigation_home)
        else {
            binding.fragmentWriteProgressBar.visibility = View.GONE
            binding.fragmentWriteElements.visibility = View.VISIBLE
            Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }
    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_basic, null)
        dialogLayout.findViewById<TextView>(R.id.textView).text = getString(R.string.confirm_exit)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        dialogTitle.findViewById<TextView>(R.id.textView).text = getString(R.string.confirm_exit_title)

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                findNavController().navigate(R.id.action_writeFragment_to_navigation_home)
            }
            setNegativeButton(getString(R.string.no)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}