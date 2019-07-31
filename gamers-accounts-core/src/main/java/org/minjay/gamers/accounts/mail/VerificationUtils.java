package org.minjay.gamers.accounts.mail;

import java.util.Random;

public abstract class VerificationUtils {

    public static String generate() {
        int n = 6;
        StringBuilder code = new StringBuilder();
        Random ran = new Random();
        for (int i = 0; i < n; i++) {
            code.append(Integer.valueOf(ran.nextInt(10)).toString());
        }
        return code.toString();
    }
}
