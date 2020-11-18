package me.young1lin.security;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/24 12:27 下午
 * @see BASE64Encoder
 * @see BASE64Decoder
 * @see Case
 */
public abstract class BaseKeyGen {

    protected static String byte2Base64(byte[] bytes) {
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(bytes);
    }

    protected static byte[] base642byte(String base64) throws IOException {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        return base64Decoder.decodeBuffer(base64);
    }

    /**
     * 数组转换为 16 进制字符串
     *
     * @param bytes
     * @param lowerOrUpper
     * @return
     */
    protected static String bytes2hex(byte[] bytes, Case lowerOrUpper) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            boolean negative = false;
            if (b < 0) {
                negative = true;
            }
            int inte = Math.abs(b);
            if (negative) {
                inte = inte | 0x80;
            }
            String temp = Integer.toHexString(inte & 0xFF);
            if (temp.length() == 1) {
                hex.append("0");
            }
            if (lowerOrUpper.equals(Case.LOWER)) {
                hex.append(temp.toLowerCase());
            } else {
                hex.append(temp.toUpperCase());
            }
        }
        return hex.toString();
    }
}

/**
 * 大写或小写
 */
enum Case {
    /**
     * 小写
     */
    LOWER,
    /**
     * 大写
     */
    UPPER;
}
