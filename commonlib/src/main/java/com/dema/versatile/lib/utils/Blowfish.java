package com.dema.versatile.lib.utils;

import java.math.BigInteger;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Blowfish {

    static String decode(String secret)
            throws Exception {
        byte[] decode;
        byte[] kbytes = "a94f1167a8142ba2769d70144b409f".getBytes();
        SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");
        BigInteger n = new BigInteger(secret, 16);
        byte[] encoding = n.toByteArray();
        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(2, key);
        decode = cipher.doFinal(encoding);
        return new String(decode);
    }

    static String encode(String secret) throws Exception {
        byte[] kbytes = "a94f1167a8142ba2769d70144b409f".getBytes();
        SecretKeySpec key = new SecretKeySpec(kbytes, "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(1, key);
        byte[] encoding = cipher.doFinal(secret.getBytes());
        BigInteger n = new BigInteger(encoding);
        return n.toString(16);
    }


    public static void main(String[] args) throws Exception  {
        byte[] bytes = "{\"key1\":\"alive\",\"key\":\"new\",\"network\":4,\"date\":\"2019-12-18 13:52:42\"}".getBytes();
        String string = encode(new String(bytes));
        String decrypt = decode(string);
        System.out.println("密文： " + string);
        System.out.println("明文： " + decrypt);


        String str = "fd5595e20c09c6d17955896a686d5bf";
        System.out.println(str.length());
    }
 
}