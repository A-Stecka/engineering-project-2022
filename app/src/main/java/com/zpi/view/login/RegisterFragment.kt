package com.zpi.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.zpi.view.MainActivity
import com.zpi.R
import com.zpi.databinding.FragmentRegisterBinding
import com.zpi.view.shared.Validator
import com.zpi.viewmodel.LoginViewModel

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var validator: Validator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        loginViewModel.getBannedWords()

        loginViewModel.bannedWords.observe(viewLifecycleOwner) { bannedWords ->
            if (bannedWords.isNotEmpty() && bannedWords[0].value != "")
                validator = Validator(requireContext(), bannedWords)
        }

        binding.fragmentRegisterLoginButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(
                    R.id.nav_host_fragment_activity_login,
                    LoginFragment()
                )
                ?.commit()
        }

        binding.fragmentRegisterRegisterButton.setOnClickListener {
            if (validate()) {
                binding.fragmentRegisterElements.visibility = View.GONE
                binding.fragmentRegisterProgressBar.visibility = View.VISIBLE
                loginViewModel.registerUser(binding.fragmentRegisterInputLogin.text.toString().trim(),
                    binding.fragmentRegisterInputPassword.text.toString().trim(),
                    binding.fragmentRegisterInputEmail.text.toString().trim(), ::registerCallback)
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        binding.signInButtonGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 100)
        }

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // pass
            }
        })

        return binding.root
    }

    private fun registerCallback(success: Boolean, ref: Int) {
        if (success) {
            Log.e("Register callback", "received ref: $ref")
            if (ref >= 0) {
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("userREF", ref)
                intent.putExtra("firstLogin", 0)
                requireActivity().startActivity(intent)
            } else {
                Toast.makeText(context, getString(R.string.error_non_unique_login), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }
        binding.fragmentRegisterElements.visibility = View.VISIBLE
        binding.fragmentRegisterProgressBar.visibility = View.GONE
    }

    private fun validate(): Boolean {
        val login = binding.fragmentRegisterInputLogin.text.toString()
        val password = binding.fragmentRegisterInputPassword.text.toString()
        val confirmPassword = binding.fragmentRegisterInputConfirmPassword.text.toString()
        val email = binding.fragmentRegisterInputEmail.text.toString()

        if (login != "" && password != "" && confirmPassword != "" && email != "") {
            if (!login.contains(' ') && !password.contains(' ')) {
                if (validator.ifValidLogin(login)) {
                    if (validator.ifValidPassword(password, 0)) {
                        if (password == confirmPassword) {
                            return if (validator.ifValidEmail(email, 0))
                                true
                            else {
                                Toast.makeText(context, getString(R.string.error_email_invalid), Toast.LENGTH_LONG).show()
                                false
                            }
                        }
                        Toast.makeText(context, getString(R.string.error_passwords_do_not_match), Toast.LENGTH_LONG).show()
                        return false
                    }
                    return false
                }
                return false
            }
            Toast.makeText(context, getString(R.string.error_no_space), Toast.LENGTH_LONG).show()
            return false
        }
        Toast.makeText(context, getString(R.string.error_empty, "Login, password, confirm password and e-mail address fields"), Toast.LENGTH_LONG).show()
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}