package csci610.inventorysystem;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author nicka
 */
public class EncryptionManager {

    public static String encryptkey = "NetworkingOnTop";

    private static SecretKeySpec secret;

    private static byte[] key;

    public static void setKey() {

        MessageDigest sha = null;
        try {
            key = encryptkey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secret = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            System.out.println("error in encrytion class, setKey");
            e.printStackTrace();
        }

    }

    public static String encrypt(String encryptMe) {

        try {
            setKey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);

            return Base64.getEncoder().encodeToString(cipher.doFinal(encryptMe.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("error in encrypt method");
            e.printStackTrace();
        }

        return null;
    }

    public static String decrypt(String decryptMe) {

        try {
            setKey();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            return new String(cipher.doFinal(Base64.getDecoder().decode(decryptMe)));
        } catch (Exception e) {
            System.out.println("error in decrypt method");
            e.printStackTrace();
        }

        return null;
    }

}
