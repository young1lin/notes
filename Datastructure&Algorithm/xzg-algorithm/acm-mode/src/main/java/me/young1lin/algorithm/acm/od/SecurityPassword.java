package me.young1lin.algorithm.acm.od;

/**
 * @author young1lin
 * @version 1.0
 * @since 2024/8/23
 */
public class SecurityPassword {

    public static void main(String[] args) {
        String password = "123<123<sa<gb";
        password = convertFinalPassword(password);
        boolean validated = validatePassword(password);
        System.out.println(validated);
    }

    private static boolean validatePassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        boolean containUpperLetter = false;
        boolean containLowerLetter = false;
        boolean containDigital = false;
        boolean containOther = false;

        // at least contains one upper case letter
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) >= 'A' && password.charAt(i) <= 'Z') {
                containUpperLetter = true;
                break;
            }
        }
        // at least contains one lower case letter
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) >= 'a' && password.charAt(i) <= 'z') {
                containLowerLetter = true;
                break;
            }
        }
        // at least contains one digital
        for (int i = 0; i < password.length(); i++) {
            if (password.charAt(i) >= '0' && password.charAt(i) <= '9') {
                containDigital = true;
                break;
            }
        }
        // at least contains one letter and digital not blank
        for (int i = 0; i < password.length(); i++) {
            if ((password.charAt(i) > '9' && password.charAt(i) < 'a') || password.charAt(i) > 'Z') {
                containOther = true;
                break;
            }
        }
        return containUpperLetter & containLowerLetter & containDigital & containOther;
    }

    private static String convertFinalPassword(String password) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (c == '<') {
                result.deleteCharAt(result.length() - 1);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

}
