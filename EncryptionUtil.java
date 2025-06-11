import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = loadKey();

    private static byte[] loadKey() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
            String key = props.getProperty("aes.key");
            if(key==null || key.length()!=16)
                throw new RuntimeErrorException(null, "AES key must be 16 characters long in config.properties");
            return key.getBytes();
        }
        catch(IOException e){
            throw new RuntimeException("Failed to load AES key from config.properteis");
        }
    }

    public static String encrypt(String data) throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyValue, ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedVal = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedVal);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyValue, ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedVal = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedVal = cipher.doFinal(decodedVal);
        return new String(decryptedVal);
    }
}