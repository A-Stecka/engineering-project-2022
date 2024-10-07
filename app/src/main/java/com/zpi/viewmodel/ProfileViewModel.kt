package com.zpi.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.zpi.model.entity.BannedWord
import com.zpi.model.entity.User
import com.zpi.model.service.BannedWordsService
import com.zpi.model.service.UserService
import java.time.LocalDateTime
import java.util.*

class ProfileViewModel : ViewModel() {
    private val userService: UserService = UserService()
    private val bannedWordsService: BannedWordsService = BannedWordsService()
    private var userRef: Int? = null

    private var _user: MutableLiveData<User> = MutableLiveData<User>().apply {
        value = User(-1, "", "", "", -1, LocalDateTime.now(), -1, "")
    }

    val user: MutableLiveData<User> = _user

    private var _bannedWords: MutableLiveData<List<BannedWord>> = MutableLiveData<List<BannedWord>>().apply {
        value = emptyList()
    }

    val bannedWords: MutableLiveData<List<BannedWord>> = _bannedWords

    fun setUserRef(userRef: Int?) {
        Log.i("ProfileViewModel got user", userRef.toString())
        this.userRef = userRef
    }

    fun setUser() {
        userService.getUser(userRef!!, ::setUser)
    }

    fun getBannedWords() {
        bannedWordsService.getBannedWords(::setBannedWords)
    }

    fun changePassword(password: String, callback: (success: Boolean) -> Unit) {
        userService.changePassword(password, user.value!!, callback, ::setUserCallback)
    }

    fun changeUsername(username: String, callback: (success: Boolean) -> Unit) {
        userService.changeUsername(username, user.value!!, callback, ::setUserCallback)
    }

    fun changeProfilePicture(profilePicture: Int, callback: (success: Boolean) -> Unit) {
        userService.changeProfilePicture(profilePicture, user.value!!, callback, ::setUserCallback)
    }

    fun changeEmail(email: String, callback: (success: Boolean) -> Unit) {
        userService.changeEmail(email, user.value!!, callback, ::setUserCallback)
    }

    private fun setUser(user: User) {
        _user.value = user
    }

    private fun setUserCallback(user: User, callback: (success: Boolean) -> Unit) {
        setUser(user)
        if (user.ref == -10)
            callback.invoke(false)
        else
            callback.invoke(true)
    }

    private fun setBannedWords(bannedWords: MutableList<BannedWord>) {
        _bannedWords.value = bannedWords
    }
}