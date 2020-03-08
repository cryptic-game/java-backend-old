package net.cryptic_game.backend.base.utils

import org.apache.commons.validator.routines.EmailValidator

object ValidationUtils {

    private val pattern = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}".toRegex()

    @JvmStatic
    fun checkMail(mail: String): Boolean = EmailValidator.getInstance().isValid(mail)

    @JvmStatic
    fun checkPassword(password: String) = pattern.matches(password)
}