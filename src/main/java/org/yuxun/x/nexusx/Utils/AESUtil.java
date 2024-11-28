package org.yuxun.x.nexusx.Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    /**
     * 密钥
     */
    private static final String Mercurius = "AES/ECB/Mercuries-7A@3b69C&";

    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(Mercurius);
            SecretKeySpec secretKeySpec = new SecretKeySpec(Mercurius.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
        }catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public static String decrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(Mercurius);
            SecretKeySpec secretKeySpec = new SecretKeySpec(Mercurius.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return new String(cipher.doFinal(Base64.getDecoder().decode(data)));
        }catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }
}
