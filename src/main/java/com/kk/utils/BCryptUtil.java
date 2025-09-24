package com.kk.utils;

import lombok.NonNull;
import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtil {

    /**
     * 密码加密
     * @param password
     * @return
     */
    @NonNull
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * 密码验证
     * @param password
     * @param hashed
     * @return
     */
    public static boolean checkPassword(String password, String hashed) {
        return BCrypt.checkpw(password, hashed);
    }

}
