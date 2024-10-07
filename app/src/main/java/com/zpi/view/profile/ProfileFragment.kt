package com.zpi.view.profile

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.zpi.databinding.FragmentProfileBinding
import com.zpi.model.MD5
import com.zpi.view.login.LoginActivity
import com.zpi.view.shared.HelpDialog
import com.zpi.view.shared.Validator
import com.zpi.viewmodel.ProfileViewModel
import androidx.annotation.NonNull
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.security.Provider


class ProfileFragment : Fragment() {
    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var validator: Validator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        mainViewModel.userRef?.observe(viewLifecycleOwner) { ref ->
            profileViewModel.setUserRef(ref)
            profileViewModel.setUser()
        }

        profileViewModel.user.observe(viewLifecycleOwner) { changedUser ->
            if (changedUser.ref != -1) {
                binding.usernameText.text = changedUser.name
                binding.profileImage.setImageResource(ImageMapper.getResource(changedUser.profilePicture))
            }
        }

        profileViewModel.getBannedWords()

        profileViewModel.bannedWords.observe(viewLifecycleOwner) { bannedWords ->
            if (bannedWords.isNotEmpty() && bannedWords[0].value != "")
                validator = Validator(requireContext(), bannedWords)
        }

        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.favouritesFrame.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_favouritesFragment)
        }

        binding.statisticsFrame.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_statsFragment)
        }

        binding.changePictureFrame.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_changePictureFragment)
        }

        binding.changeNameFrame.setOnClickListener {
            if (profileViewModel.user.value != null)
                showChangeNameDialog()
            else
                errorConnection()
        }

        binding.changePasswordFrame.setOnClickListener {
            if (profileViewModel.user.value != null)
                showChangePasswordDialog()
            else
                errorConnection()
        }

        binding.changeEmailFrame.setOnClickListener {
            if (profileViewModel.user.value != null)
                showChangeEmailDialog()
            else
                errorConnection()
        }

        binding.aboutAppFrame.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_aboutFragment)
        }

        binding.helpFrame.setOnClickListener {
            HelpDialog(requireContext(), layoutInflater).show()
        }

        binding.logOutFrame.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build()
            val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity().applicationContext, gso)
            val account = GoogleSignIn.getLastSignedInAccount(requireActivity().applicationContext)
            Log.i("google account", account.toString())
            if (account != null)
                mGoogleSignInClient.signOut()
            val intent = Intent(context, LoginActivity::class.java)
            requireActivity().startActivity(intent)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // pass
            }
        })
    }

    private fun showChangePasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_change_password, null)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        dialogTitle.findViewById<TextView>(R.id.textView).text = getString(R.string.change_password)

        val previousPassword = dialogLayout.findViewById<EditText>(R.id.dialog_change_password_input_password)
        val newPassword = dialogLayout.findViewById<EditText>(R.id.dialog_change_password_input_new_password)
        val confirmPassword = dialogLayout.findViewById<EditText>(R.id.dialog_change_password_confirm_password)

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(getString(R.string.save)) { _, _ -> }
            setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                if (previousPassword.text.toString() == "" || newPassword.text.toString() == "" || confirmPassword.text.toString() == "") {
                    Toast.makeText(requireContext(), getString(R.string.error_empty, "Old password, new password and confirm new password fields"), Toast.LENGTH_SHORT).show()
                }
                else {
                    if (MD5.md5(previousPassword.text.toString()) != profileViewModel.user.value!!.password) {
                        Toast.makeText(requireContext(), getString(R.string.error_incorrect_password), Toast.LENGTH_SHORT).show()
                    }
                    else {
                        if (validator.ifValidPassword(newPassword.text.toString(), 1)) {
                            if (newPassword.text.toString() != confirmPassword.text.toString()) {
                                Toast.makeText(requireContext(), getString(R.string.error_passwords_do_not_match), Toast.LENGTH_SHORT).show()
                            }
                            else {
                                profileViewModel.changePassword(newPassword.text.toString(), ::passwordCallback)
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showChangeNameDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_change_name, null)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        dialogTitle.findViewById<TextView>(R.id.textView).text = getString(R.string.change_name)

        val previousUsername = dialogLayout.findViewById<TextView>(R.id.dialog_change_label)
        previousUsername.text = getString(R.string.current_name, profileViewModel.user.value!!.name)

        val newUsername = dialogLayout.findViewById<EditText>(R.id.dialog_change_input)

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(getString(R.string.save))  { _, _ -> }
            setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                if (validator.ifValidName(newUsername.text.toString())) {
                    profileViewModel.changeUsername(newUsername.text.toString(), ::usernameCallback)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun showChangeEmailDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_change_email, null)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        dialogTitle.findViewById<TextView>(R.id.textView).text = getString(R.string.change_email)

        dialogLayout.findViewById<TextView>(R.id.dialog_change_current_email).text = profileViewModel.user.value!!.email

        val newEmail = dialogLayout.findViewById<EditText>(R.id.dialog_change_input)

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(getString(R.string.save))  { _, _ -> }
            setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                if (validator.ifValidEmail(newEmail.text.toString(), 1)) {
                    profileViewModel.changeEmail(newEmail.text.toString(), ::emailCallback)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun passwordCallback(success: Boolean) {
        if (!success)
            errorConnection()
        else
            Toast.makeText(requireContext(), getString(R.string.success_password_changed), Toast.LENGTH_SHORT).show()
    }

    private fun usernameCallback(success: Boolean) {
        if (!success)
            errorConnection()
        else
            Toast.makeText(requireContext(), getString(R.string.success_username_changed), Toast.LENGTH_SHORT).show()
    }

    private fun emailCallback(success: Boolean) {
        if (!success)
            errorConnection()
        else
            Toast.makeText(requireContext(), getString(R.string.success_email_changed), Toast.LENGTH_SHORT).show()
    }

    private fun errorConnection() {
        Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
    }

}
