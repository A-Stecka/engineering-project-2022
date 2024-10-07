package com.zpi.view.shared

import android.content.Context
import android.widget.Toast
import com.zpi.R
import com.zpi.model.entity.BannedWord
import java.util.regex.Pattern

class Validator(private val context: Context, private val bannedWords: List<BannedWord>) {
    private val minPasswordLength = 8
    private val maxLengthShort = 25
    private val maxLengthMedium = 50
    private val maxLengthLong = 250
    private val maxLengthVeryLong = 3000

    fun ifValidName(name: String): Boolean {
        if (name == "") {
            Toast.makeText(context, context.getString(R.string.error_empty, "New name"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (name.length > maxLengthShort) {
            Toast.makeText(context, context.getString(R.string.error_input_too_long, "New name", maxLengthShort), Toast.LENGTH_SHORT).show()
            return false
        }
        return checkForBannedWords(name)
    }

    fun ifValidLogin(login: String): Boolean {
        if (login.length > maxLengthShort) {
            Toast.makeText(context, context.getString(R.string.error_input_too_long, "Login", maxLengthShort), Toast.LENGTH_SHORT).show()
            return false
        }
        return checkForBannedWords(login)
    }

    fun ifValidPassword(password: String, mode: Int): Boolean {
        if (password.length > maxLengthShort) {
            Toast.makeText(context, context.getString(R.string.error_input_too_long, if (mode == 0) "Password" else "New password", maxLengthShort), Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.length >= minPasswordLength) {
            val lettersDigits: Pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")
            val specials: Pattern = Pattern.compile("^(?=.*[.,_!@#$%^&*+]).+$")
            if (lettersDigits.matcher(password).matches() && (specials.matcher(password).matches() || password.contains('-')))
                return true
            Toast.makeText(context, context.getString(R.string.error_weak_password), Toast.LENGTH_LONG).show()
            return false
        }
        Toast.makeText(context, context.getString(R.string.error_short_password), Toast.LENGTH_LONG).show()
        return false
    }

    fun ifValidEmail(email: String, mode: Int): Boolean {
        if (email == "") {
            Toast.makeText(context, context.getString(R.string.error_empty, if (mode == 0) "E-mail address" else "New e-mail address"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.length > maxLengthMedium) {
            Toast.makeText(context, context.getString(R.string.error_input_too_long, if (mode == 0) "E-mail address" else "New e-mail address", maxLengthMedium), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, context.getString(R.string.error_email_invalid), Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun ifValidComment(comment: String): Boolean {
        if (comment == "") {
            Toast.makeText(context, context.getString(R.string.error_empty, "Comment"), Toast.LENGTH_SHORT).show()
            return false
        }
        if (comment.length > maxLengthLong) {
            Toast.makeText(context, context.getString(R.string.error_input_too_long, "Comment", maxLengthLong), Toast.LENGTH_SHORT).show()
            return false
        }
        return checkForBannedWords(comment)
    }

    fun ifValidStory(content: String): Boolean {
        if (content.length > maxLengthVeryLong) {
            Toast.makeText(context, context.getString(R.string.error_input_too_long, "Story", maxLengthVeryLong), Toast.LENGTH_SHORT).show()
            return false
        }
        return checkForBannedWords(content)
    }

    fun ifValidTitle(title: String): Boolean {
        if (title.length > maxLengthMedium) {
            Toast.makeText(context, context.getString(R.string.error_input_too_long, "Title", maxLengthMedium), Toast.LENGTH_SHORT).show()
            return false
        }
        return checkForBannedWords(title)
    }

    private fun checkForBannedWords(text: String): Boolean {
        var message = ""
        for (word in bannedWords)
            if (word.value in text)
                message += word.censored + ", "
        message = message.trimEnd()
        message = message.trimEnd(',')
        if (message == "")
            return true
        Toast.makeText(context, context.getString(R.string.error_banned_word, message), Toast.LENGTH_LONG).show()
        return false
    }

}