package com.dtranx.tools.licensecreate.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.KeyGenerator;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * @version V1.0
 * @desc AES 加密工具类
 */
public class AESUtils {

    /**
     * RSA加密后的AES秘钥
     */
    private static String aesEncyptPwd="ZIkun+KvXFWLZLYUwXqFWazQeRe119AkcGcl+p8Erzi4EEaHBFYcQuGuKthIE+1IWSQxoUpUJkT0T1+xtoRi3txDnBikdrFhccGZdRpqwRv58q5nqxJX4wVrq0Ms02KBKgQRTqqlzfYLzQcYPyhv8KPE8JDVkttic+W+j5pFles=";

    private static final String KEY_ALGORITHM = "AES";

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    /**
     * AES 加密操作
     *
     * @param content  待加密内容
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(RSAUtils.decrypt(aesEncyptPwd)));
            byte[] result = cipher.doFinal(byteContent);
            return Base64Utils.encodeToString(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("AES加密失败");
        }

        return null;
    }

    /**
     * AES 解密操作
     *
     * @param content  已加密内容
     * @return
     */
    public static String decrypt(String content) {
        try {
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            //使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(RSAUtils.decrypt(aesEncyptPwd)));
            //执行操作
            byte[] result = cipher.doFinal(Base64Utils.decodeFromString(content));
            return new String(result, "utf-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("AES解密失败");
        }
        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(String aesKey) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            //AES 要求密钥长度为 128
            SecureRandom random=SecureRandom.getInstance("SHA1PRNG","SUN");
            random.setSeed(aesKey.getBytes());
            kg.init(128, random);
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            System.out.println("生成加密秘钥失败");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            System.out.println("生成加密秘钥失败");
        }
        return null;
    }


    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        //系统标识
        String s = "A8:A1:59:41:89:36-BFEBFBFF000906EA";
        String systemSign = AESUtils.encrypt(s);
        System.out.println("系统标识串：" +systemSign);
    }
}