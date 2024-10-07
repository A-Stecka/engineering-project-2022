package com.zpi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.zpi.model.entity.BannedWord
import com.zpi.model.entity.User
import com.zpi.model.service.BannedWordsService
import com.zpi.model.service.UserService
import java.time.LocalDateTime

class LoginViewModel : ViewModel() {

    private val bannedWordsService: BannedWordsService = BannedWordsService()
    private val userService: UserService = UserService()

    private var _bannedWords: MutableLiveData<List<BannedWord>> = MutableLiveData<List<BannedWord>>().apply {
        value = emptyList()
    }

    val bannedWords: MutableLiveData<List<BannedWord>> = _bannedWords

    fun getBannedWords() {
        bannedWordsService.getBannedWords(::setBannedWords)
    }

    fun loginUser(login: String, password: String, callback: (success: Boolean, ref: Int) -> Unit) {
        userService.getUserPassword(login, password, callback)
    }

    fun getUserEmail(login: String, callback: (success: Boolean, ref: Int, email: String) -> Unit) {
        userService.getUserEmail(login, callback)
    }

    fun getUserLogins(email: String, callback: (success: Boolean, logins: String, email: String) -> Unit) {
        userService.getUserLogins(email, callback)
    }

    fun registerUser(login: String, password: String, email: String, callback: (success: Boolean, ref: Int) -> Unit) {
        val user = User(-1, login, password, login, 1, LocalDateTime.now(), 0, email)
        userService.registerUser(user, callback)
    }

    fun loginWithGoogle(login: String, name: String, callback: (success: Boolean, login: String, name: String, ref: Int) -> Unit) {
        userService.loginUserGoogle(login, name, callback)
    }

    fun registerUserGoogle(login: String, name: String, callback: (success: Boolean, ref: Int) -> Unit) {
        val user = User(-1, login, "", name, 1, LocalDateTime.now(), 1, "")
        userService.registerUser(user, callback)
    }

    private fun setBannedWords(bannedWords: MutableList<BannedWord>) {
        _bannedWords.value = bannedWords
    }

}