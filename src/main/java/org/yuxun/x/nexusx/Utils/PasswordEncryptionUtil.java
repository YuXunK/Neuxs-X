package org.yuxun.x.nexusx.Utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 *
 */
public class PasswordEncryptionUtil {
     public static String encryptPassword(String password)  {

         String encryptedPassword;

         BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

         return encryptedPassword = encoder.encode(password);
    }
}
