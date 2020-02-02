package net.cryptic_game.backend.base.utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;

public class ValidationUtils {

    public static boolean checkMail(String mail) {
        return EmailValidator.getInstance().isValid(mail);
    }

    public static boolean checkPassword(String password) {
        return Pattern.compile("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,}").matcher(password).find();
    }
}
