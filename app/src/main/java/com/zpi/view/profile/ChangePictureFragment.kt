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
import com.zpi.databinding.FragmentChangePictureBinding
import com.zpi.viewmodel.ProfileViewModel

class ChangePictureFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentChangePictureBinding? = null
    private val binding get() = _binding!!

    private lateinit var gridAdapter: GridAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChangePictureBinding.inflate(inflater, container, false)

        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        ViewModelProvider(requireActivity())[MainViewModel::class.java].userRef?.observe(viewLifecycleOwner) { ref ->
            profileViewModel.setUserRef(ref)
            profileViewModel.setUser()
        }

        profileViewModel.user.observe(viewLifecycleOwner) { changedUser ->
            if (changedUser.ref != -1)
                binding.fragmentPictureCurrentImage.setImageResource(ImageMapper.getResource(changedUser.profilePicture))
            else if (changedUser.ref == -10)
                Toast.makeText(requireContext(), getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }

        binding.fragmentPictureBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_changePictureFragment_to_navigation_profile)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_changePictureFragment_to_navigation_profile)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gridAdapter = GridAdapter(requireContext())
        binding.fragmentPicturePicturesGrid.apply{
            adapter = gridAdapter
            setOnItemClickListener { _, _, position, _ ->
                val item = (adapter as GridAdapter).getItemInt(position)
                binding.fragmentPictureCurrentImage.setImageResource(item)
                profileViewModel.changeProfilePicture(ImageMapper.getIndex(item), ::profilePictureCallback)
            }
        }
    }

    private fun profilePictureCallback(success: Boolean) {
        if (!success)
            Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        else
            Toast.makeText(requireContext(), getString(R.string.success_profile_picture_changed), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
