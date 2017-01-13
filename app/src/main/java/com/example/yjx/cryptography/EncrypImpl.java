package com.example.yjx.cryptography;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by yjx on 2016-12-20.
 */
public class EncrypImpl {
    //    SecretKey deskey = null;// 加解密必须使用相同的key，所以需要用实例变量控制
//
//    public static String decryption(byte[] s) {
//        SecretKeySpec deskey = new SecretKeySpec(s,"AES");
//        System.out.println("解密开始...");
//        Cipher c = null;
//        byte[] enc = null;
//        try {
//            // c = Cipher.getInstance("DESede");
//            c = Cipher.getInstance("AES");
//            c.init(Cipher.ENCRYPT_MODE,deskey);
//            enc = c.doFinal();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
////        try {
////            c.init(Cipher.DECRYPT_MODE, deskey);
////            enc = c.doFinal(s);
////            System.out.println("\t解密之后的明文是:" + new String(enc));
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//        return new String(enc);
//    }
//
//    public byte[] encryption(String s) {
//        System.out.println("加密开始...");
//        System.out.println("\t原文：" + s);
//        KeyGenerator keygen;
//        Cipher c = null;
//        try {
//            // keygen = KeyGenerator.getInstance("DESede");
//            keygen = KeyGenerator.getInstance("AES");
//            deskey = keygen.generateKey();
//            // c = Cipher.getInstance("DESede");
//            c = Cipher.getInstance("AES");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        byte[] dec = null;
//        try {
//            c.init(Cipher.ENCRYPT_MODE, deskey);
//            dec = c.doFinal(s.getBytes());
//            System.out.print("\t加密后密文是:");
//            for (byte b : dec) {
//                System.out.print(b + ",");
//            }
//            System.out.println();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return dec;
//    }
    public static String encryptAES(String seed, String cleartext) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes("UTF-8"));
        byte[] result = encryptAES(rawKey, cleartext.getBytes("UTF-8"));
        return new String(Base64.encode(result, 0));
        //return toHex_0(result);
    }

    private static byte[] encryptAES(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    public static String decryptAES(String seed, String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seed.getBytes("UTF-8"));
        byte[] enc = Base64.decode(encrypted, 0);
        //byte[] enc = toByte_0(encrypted);
        byte[] result = decryptAES(rawKey, enc);
        return new String(result, "UTF-8");
    }

    private static byte[] decryptAES(byte[] raw, byte[] encrypted) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;

    }
}
