package com.zpi.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.zpi.view.MainActivity
import com.zpi.R
import com.zpi.databinding.ActivityLoginBinding
import com.zpi.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_login, LoginFragment())
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
        handleSignInResult(task)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            loginViewModel.loginWithGoogle(account.id!!, account.displayName!!, ::loginCallback)
        } catch (e: ApiException) {
            Log.w("LOGIN", "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun loginCallback(success: Boolean, login: String, name: String, ref: Int) {
        if (success) {
            if (ref >= 0) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("userREF", ref)
                intent.putExtra("firstLogin", 1)
                this.startActivity(intent)
            } else {
                loginViewModel.registerUserGoogle(login, name, ::registerGoogleCallback)
            }
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }
    }

    private fun registerGoogleCallback(success: Boolean, ref: Int) {
        if (success) {
            if (ref >= 0) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("userREF", ref)
                intent.putExtra("firstLogin", 0)
                this.startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.error_non_unique_login), Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, getString(R.string.error_connection), Toast.LENGTH_LONG).show()
        }
    }
}