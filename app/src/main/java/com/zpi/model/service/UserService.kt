package com.zpi.model.service

import android.util.Log
import com.zpi.model.Converter
import com.zpi.model.MD5
import com.zpi.model.dto.*
import com.zpi.model.entity.User
import com.zpi.model.entity.LeaderboardItem
import com.zpi.model.entity.Prompt
import com.zpi.model.entity.Story
import com.zpi.model.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class UserService {
    private var apiService: UserRepository = UserRepository.create(UserRepository::class.java)

    fun getUserPassword(login: String, password: String, callback: (success: Boolean, ref: Int) -> Unit) {
        apiService.getUserPassword(login)?.enqueue(object : Callback<GetUserPasswordDTO?> {

            override fun onResponse(call: Call<GetUserPasswordDTO?>, response: Response<GetUserPasswordDTO?>) {
                Log.i("Login attempt response", response.message() + ": " + response.body())
                if (response.body() != null) {
                    if (response.body()!!.ref != -1) {
                        if (response.body()!!.password == MD5.md5(password))
                            callback.invoke(true, response.body()!!.ref!!)
                        else
                            callback.invoke(true, -1)
                    } else {
                        callback.invoke(false, 0)
                    }
                } else {
                    callback.invoke(false, 0)
                }
            }

            override fun onFailure(call: Call<GetUserPasswordDTO?>, t: Throwable) {
                Log.i("Login attempt failed", "")
                callback.invoke(false, -1)
            }
        })
    }

    fun getLeaderboard(callback: (leaders: List<LeaderboardItem>) -> Unit) {
        apiService.getLeaderboard()?.enqueue(object : Callback<List<LeaderboardItemDTO>?> {

            override fun onResponse(call: Call<List<LeaderboardItemDTO>?>, response: Response<List<LeaderboardItemDTO>?>) {
                if (response.body() != null) {
                    val leaders: List<LeaderboardItem> = Converter.convertToLeaderboardList(response.body()!!)
                    callback.invoke(leaders)
                }
            }
            override fun onFailure(call: Call<List<LeaderboardItemDTO>?>, t: Throwable) {
                // not needed
            }

        })
    }

    fun getUserEmail(login: String, callback: (success: Boolean, ref: Int, email: String) -> Unit) {
        apiService.getUserEmail(login)?.enqueue(object : Callback<GetUserEmailDTO?> {

            override fun onResponse(call: Call<GetUserEmailDTO?>, response: Response<GetUserEmailDTO?>) {
                Log.i("Forgot password attempt response", response.message() + ": " + response.body())
                if (response.body() != null) {
                    if (response.body()!!.ref != -1) {
                        callback.invoke(true, response.body()!!.ref!!, response.body()!!.email!!)
                    }
                    else {
                        callback.invoke(false, -1, "")
                    }
                } else {
                    callback.invoke(false, -1, "")
                }
            }

            override fun onFailure(call: Call<GetUserEmailDTO?>, t: Throwable) {
                Log.i("Forgot password attempt failed", "")
                callback.invoke(false, 0, "")
            }
        })
    }

    fun getUserLogins(email: String, callback: (success: Boolean, logins: String, email: String) -> Unit) {
        apiService.getUserLogins(email)?.enqueue(object : Callback<GetUserLoginsDTO?> {

            override fun onResponse(call: Call<GetUserLoginsDTO?>, response: Response<GetUserLoginsDTO?>) {
                Log.i("Forgot login attempt response", response.message() + ": " + response.body())
                if (response.body() != null) {
                    if (response.body()!!.logins != "") {
                        callback.invoke(true, response.body()!!.logins!!, email)
                    }
                    else {
                        callback.invoke(false, "", email)
                    }
                } else {
                    callback.invoke(false, "", email)
                }
            }

            override fun onFailure(call: Call<GetUserLoginsDTO?>, t: Throwable) {
                Log.i("Forgot login attempt failed", "")
                callback.invoke(false, "error", "")
            }
        })
    }

    fun loginUserGoogle(login: String, name: String, callback: (success: Boolean, login: String, name: String, ref: Int) -> Unit) {
        apiService.getUserRef(login)?.enqueue(object : Callback<RefDTO?> {

            override fun onResponse(call: Call<RefDTO?>, response: Response<RefDTO?>) {
                Log.i("Login attempt response", response.message() + ": " + response.body())
                if (response.body() != null) {
                    callback.invoke(true, login, name, response.body()!!.ref!!)
                } else {
                    callback.invoke(true, login, name, -1)
                }
            }

            override fun onFailure(call: Call<RefDTO?>, t: Throwable) {
                callback.invoke(false, login, name, -1)
            }
        })
    }

    fun registerUser(user: User, callback: (success: Boolean, ref: Int) -> Unit) {
        val userDTO = Converter.convertToUserDTO(user, true)
        apiService.registerUser(userDTO)?.enqueue(object : Callback<RefDTO?> {

            override fun onResponse(call: Call<RefDTO?>, response: Response<RefDTO?>) {
                Log.i("Register attempt response", response.message() + ": " + response.body())
                if (response.body() != null) {
                    callback.invoke(true, response.body()!!.ref!!)
                } else {
                    callback.invoke(true, -1)
                }
            }

            override fun onFailure(call: Call<RefDTO?>, t: Throwable) {
                Log.i("Register attempt failed", "")
                callback.invoke(false, -1)
            }
        })
    }

    fun getUser(ref: Int, callback: (user: User) -> Unit) {
        apiService.getUser(ref)?.enqueue(object : Callback<UserDTO?> {
            override fun onResponse(call: Call<UserDTO?>, response: Response<UserDTO?>) {
                if (response.body() != null) {
                    val responseDTO: UserDTO = response.body()!!
                    callback.invoke(Converter.convertToUser(responseDTO))
                } else {
                    callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""))
                }
            }

            override fun onFailure(call: Call<UserDTO?>, t: Throwable) {
                callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""))
            }
        })
    }

    fun changePassword(password: String, user: User, successCallback: (success: Boolean) -> Unit, callback: (user: User, callback: (success: Boolean) -> Unit) -> Unit) {
        val newUser = User(user.ref, user.login, password, user.name, user.profilePicture, user.regDate, user.userType, user.email)
        val userDTO = Converter.convertToUserDTO(newUser, true)
        apiService.changePassword(userDTO)?.enqueue(object : Callback<UserDTO?> {
            override fun onResponse(call: Call<UserDTO?>, response: Response<UserDTO?>) {
                if (response.body() != null) {
                    val responseDTO: UserDTO = response.body()!!
                    callback.invoke(Converter.convertToUser(responseDTO), successCallback)
                } else {
                    callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""), successCallback)
                }
            }

            override fun onFailure(call: Call<UserDTO?>, t: Throwable) {
                callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""), successCallback)
            }
        })
    }

    fun changeUsername(username: String, user: User, successCallback: (success: Boolean) -> Unit, callback: (user: User, callback: (success: Boolean) -> Unit) -> Unit) {
        val newUser = User(user.ref, user.login, user.password, username, user.profilePicture, user.regDate, user.userType, user.email)
        val userDTO = Converter.convertToUserDTO(newUser, false)
        apiService.changeUsername(userDTO)?.enqueue(object : Callback<UserDTO?> {
            override fun onResponse(call: Call<UserDTO?>, response: Response<UserDTO?>) {
                if (response.body() != null) {
                    val responseDTO: UserDTO = response.body()!!
                    callback.invoke(Converter.convertToUser(responseDTO), successCallback)
                } else {
                    callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""), successCallback)
                }
            }

            override fun onFailure(call: Call<UserDTO?>, t: Throwable) {
                callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""), successCallback)
            }
        })
    }

    fun changeProfilePicture(profilePicture: Int, user: User, successCallback: (success: Boolean) -> Unit, callback: (user: User, callback: (success: Boolean) -> Unit) -> Unit) {
        val newUser = User(user.ref, user.login, user.password, user.name, profilePicture, user.regDate, user.userType, user.email)
        val userDTO = Converter.convertToUserDTO(newUser, false)
        apiService.changeProfilePicture(userDTO)?.enqueue(object : Callback<UserDTO?> {
            override fun onResponse(call: Call<UserDTO?>, response: Response<UserDTO?>) {
                if (response.body() != null) {
                    val responseDTO: UserDTO = response.body()!!
                    callback.invoke(Converter.convertToUser(responseDTO), successCallback)
                } else {
                    callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""), successCallback)
                }
            }

            override fun onFailure(call: Call<UserDTO?>, t: Throwable) {
                callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""), successCallback)
            }
        })
    }

    fun changeEmail(email: String, user: User, successCallback: (success: Boolean) -> Unit, callback: (user: User, callback: (success: Boolean) -> Unit) -> Unit) {
        val userDTO = UserDTO(
            user.ref, user.login, user.password, user.name, user.profilePicture,
            user.regDate.toString(), user.userType, email
        )
        apiService.changeEmail(userDTO)?.enqueue(object : Callback<UserDTO?> {
            override fun onResponse(call: Call<UserDTO?>, response: Response<UserDTO?>) {
                if (response.body() != null) {
                    val responseDTO: UserDTO = response.body()!!
                    callback.invoke(Converter.convertToUser(responseDTO), successCallback)
                } else {
                    callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""), successCallback)
                }
            }

            override fun onFailure(call: Call<UserDTO?>, t: Throwable) {
                callback.invoke(User(-10,"","","",-1, LocalDateTime.now(), -1, ""), successCallback)
            }
        })
    }
}
