package com.sysu.sjk.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by sjk on 16-11-1.
 */
public class StringUtils {

    public static String getHashByMD5(String originString) {
        String ret;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(originString.getBytes("UTF-8"));
            byte[] bytes = messageDigest.digest();
            int len = bytes.length;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; ++i) {
                String tmp = Integer.toHexString(0xff & bytes[i]);
                if (tmp.length() == 1) {
                    sb.append('0');
                }
                sb.append(tmp);
            }
            ret = sb.toString();
        } catch (NoSuchAlgorithmException nsae) {
            ret = originString;
        } catch (UnsupportedEncodingException uee) {
            ret = originString;
        }
        return ret;
    }
}
