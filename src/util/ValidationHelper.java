package util;

import javax.swing.JTextField;

public class ValidationHelper {
    public static boolean validateLoginInput(String username, String password) {
        return !username.isEmpty() && !password.isEmpty();
    }

    public static boolean validateTextField(JTextField field, int minLength) {
        return field.getText().trim().length() >= minLength;
    }

    public static boolean validatePassword(char[] password, int minLength) {
        return password.length >= minLength;
    }
}