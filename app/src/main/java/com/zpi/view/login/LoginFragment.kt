package com.zpi.view.login

import android.content.Context
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.zpi.view.MainActivity
import com.zpi.R
import com.zpi.databinding.FragmentLoginBinding
import java.lang.Exception
import com.zpi.view.login.email.GMailSender
import com.zpi.view.shared.Validator
import com.zpi.viewmodel.LoginViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var validator: Validator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        loginViewModel = ViewModelProvider(activity!!)[LoginViewModel::class.java]

        loginViewModel.getBannedWords()

        loginViewModel.bannedWords.observe(viewLifecycleOwner) { bannedWords ->
            if (bannedWords.isNotEmpty() && bannedWords[0].value != "")
                validator = Validator(requireContext(), bannedWords)
        }

        binding.fragmentLoginRegisterButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(
                    R.id.nav_host_fragment_activity_login,
                    RegisterFragment()
                )
                ?.commit()
        }

        binding.fragmentLoginLoginButton.setOnClickListener {
            if (validate()) {
                binding.fragmentLoginElements.visibility = View.GONE
                binding.fragmentLoginProgressBar.visibility = View.VISIBLE
                loginViewModel.loginUser(
                    binding.fragmentLoginInputLogin.text.toString().trim(),
                    binding.fragmentLoginInputPassword.text.toString().trim(), ::loginCallback
                )
            }
        }

        binding.fragmentLoginForgotPasswordButton.setOnClickListener {
            showForgotPasswordDialog()
        }

        binding.fragmentLoginForgotLoginButton.setOnClickListener {
            showForgotLoginDialog()
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity().applicationContext, gso)
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

    private fun showForgotPasswordDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_forgot_password, null)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        dialogTitle.findViewById<TextView>(R.id.textView).text = getString(R.string.forgot_password_label)

        val login = dialogLayout.findViewById<EditText>(R.id.dialog_forgot_password_input)

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(getString(R.string.confirm)) { _, _ -> }
            setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            confirmButton.setOnClickListener {
                if (login.text.toString() != "") {
                    if (validator.ifValidLogin(login.text.toString())) {
                        loginViewModel.getUserEmail(login.text.toString(), ::passwordEmailCallback)
                        dialog.dismiss()
                    }
                } else {
                    Toast.makeText(context, getString(R.string.error_empty, "Login"), Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun showForgotLoginDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater: LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_forgot_login, null)
        val dialogTitle = inflater.inflate(R.layout.dialog_title, null)
        dialogTitle.findViewById<TextView>(R.id.textView).text = getString(R.string.forgot_login_label)

        val email = dialogLayout.findViewById<EditText>(R.id.dialog_forgot_login_input)

        val dialog: AlertDialog = with(builder) {
            setCustomTitle(dialogTitle)
            setPositiveButton(getString(R.string.confirm)) { _, _ -> }
            setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            setView(dialogLayout)
            create()
        }

        dialog.setCanceledOnTouchOutside(false)

        dialog.setOnShowListener {
            val confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            confirmButton.setOnClickListener {
                if (validator.ifValidEmail(email.text.toString(), 0)) {
                    loginViewModel.getUserLogins(email.text.toString(), ::loginEmailCallback)
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun loginCallback(success: Boolean, ref: Int) {
        if (success) {
            Log.i("Login ref", ref.toString())
            if (ref >= 0) {
                val intent = Intent(context, MainActivity::class.java)
                intent.putExtra("userREF", ref)
                intent.putExtra("firstLogin", 1)
                requireActivity().startActivity(intent)
            } else {
                Toast.makeText(context, getString(R.string.error_incorrect_auth), Toast.LENGTH_LONG).show()
            }
        } else {
            if (ref == -1)
                Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
            else
                Toast.makeText(context, getString(R.string.error_no_user), Toast.LENGTH_LONG).show()
        }
        binding.fragmentLoginElements.visibility = View.VISIBLE
        binding.fragmentLoginProgressBar.visibility = View.GONE
    }

    private fun validate(): Boolean {
        val login = binding.fragmentLoginInputLogin.text.toString()
        val password = binding.fragmentLoginInputPassword.text.toString()
        if (login != "" && password != "")
            return true
        Toast.makeText(context, getString(R.string.error_empty, "Login and password fields"), Toast.LENGTH_LONG).show()
        return false
    }

    private fun passwordEmailCallback(success: Boolean, ref: Int, address: String) {
        if (success) {
            sendEmail(
                address,
                getString(R.string.email_subject_password),
                getString(R.string.email_content_password, ref.toString()))
            Toast.makeText(context, getString(R.string.success_email), Toast.LENGTH_LONG).show()
        } else {
            if (ref == -1)
                Toast.makeText(context, getString(R.string.error_no_user), Toast.LENGTH_LONG).show()
            else
                Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }
    }

    private fun loginEmailCallback(success: Boolean, logins: String, address: String) {
        if (success) {
            sendEmail(address, getString(R.string.email_subject_login), getString(R.string.email_content_login, logins))
            Toast.makeText(context, getString(R.string.success_email), Toast.LENGTH_LONG).show()
        } else {
            if (logins == "")
                Toast.makeText(context, getString(R.string.error_no_email), Toast.LENGTH_LONG).show()
            else
                Toast.makeText(context, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }
    }

    private fun sendEmail(address: String, title: String, content: String) {
        Thread {
            try {
                val sender = GMailSender(getString(R.string.email_address), getString(R.string.email_app_password))
                sender.sendMail(title, content, getString(R.string.email_address), address)
            } catch (e: Exception) {
                Log.e("LoginFragment email error", e.stackTraceToString())
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}