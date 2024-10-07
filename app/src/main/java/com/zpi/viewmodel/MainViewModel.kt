package com.zpi.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient

class MainViewModel : ViewModel() {

    private var loggedInUserREF: MutableLiveData<Int?>? = MutableLiveData(-1)

    fun setLoggedIn(userREF: Int) {
        loggedInUserREF?.value = userREF
    }

    val userRef: MutableLiveData<Int?>?
        get() = loggedInUserREF

    private var _firstLogin: MutableLiveData<Int?>? = MutableLiveData(-1)

    fun setFirstLogin(firstLogin: Int) {
        _firstLogin?.value = firstLogin
    }

    val firstLogin: MutableLiveData<Int?>?
        get() = _firstLogin

}