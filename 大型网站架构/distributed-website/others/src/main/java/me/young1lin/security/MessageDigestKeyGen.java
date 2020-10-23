package me.young1lin.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/19 7:29 上午
 */
public abstract class MessageDigestKeyGen extends BaseKeyGen {

    private MessageDigestKeyGen() {
    }

    /**
     * 获取 MD5 大写值
     *
     * @param content
     * @return  MD5 大写值
     */
    public static String getMd5UpperString(String content) {
        StringBuilder sb = new StringBuilder(20);
        byte[] bytes = {};
        try {
            bytes = getMD5ByteArray(content);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes2hex(bytes, Case.UPPER);
    }

    /**
     * 获取 MD5 小写值
     *
     * @param content
     * @return string MD5 小写值
     */
    public static String getMd5LowCaseString(String content) {
        StringBuilder sb = new StringBuilder(20);
        byte[] bytes = {};
        try {
            bytes = getMD5ByteArray(content);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytes2hex(bytes, Case.LOWER);
    }

    private static byte[] getMD5ByteArray(String content) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return md5.digest(content.getBytes("utf8"));
    }

    private static byte[] getSHA1Bytes(String content) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
        return sha1.digest(content.getBytes("utf8"));
    }

    public static void main(String[] args) {
        String content = "jin tian shui mian bu haohkjkjljjjkjljljljkjkljljj ";
        String str = getMd5UpperString(content);
        System.out.println(str);
        String str1 = getMd5LowCaseString(content);
        System.out.println(str1);
    }
}

