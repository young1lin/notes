package me.young1lin.security;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 对称加密 des
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/8/24 12:25 下午
 */
public abstract class KeyGenDes extends BaseKeyGen {

    private KeyGenDes() {
        // 防止反射调用，防止实例化
        throw new RuntimeException();
    }

    /**
     * 生成密钥
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String genKeyDes() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);
        SecretKey key = keyGen.generateKey();
        return byte2Base64(key.getEncoded());
    }

    public static SecretKey loadKeyDes(String base64Key) throws IOException {
        byte[] bytes = base642byte(base64Key);
        return new SecretKeySpec(bytes, "DES");
    }

    /**
     * 加密
     *
     * @param source
     * @param key
     * @return
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static byte[] encryptDES(byte[] source, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException
            , InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(source);
    }

    public static byte[] decryptDES(byte[] source, SecretKey key) throws NoSuchPaddingException, NoSuchAlgorithmException
            , InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(source);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException
            , InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        String str = "hello i'm ";
        String s = genKeyDes();
        SecretKey key = loadKeyDes(s);
        byte[] bytes = str.getBytes("UTF-8");
        String encryptDes = byte2Base64(encryptDES(bytes, key));
        System.out.println(encryptDes);
        byte[] bytes1 = decryptDES(base642byte(encryptDes), key);
        System.out.println(Arrays.toString(bytes1));
        String s1 = new String(bytes1, Charset.forName("UTF-8"));
        System.out.println(s1);
        // h ascii 104
        // e ascii 101
        // l ascii 108
        // q 96
    }
}
