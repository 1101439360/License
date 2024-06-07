package com.phh.tools.licensecreate.utils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * @author penghao
 * @Description 生成公钥私钥对
 * @createDate 2022/05/05
 * @createTime 14:25
 */
public class KeyGenerator {

    /**
     * 私钥
     */
    private static byte[] privateKey;

    /**
     * 公钥
     */
    private static byte[] publicKey;

    /**
     * 加密算法
     */
    private static final String KEY_ALGORITHM = "RSA";

    public void generater() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGenerator.initialize(1024);
            KeyPair keyPair = keyPairGenerator.genKeyPair();
            RSAPublicKey pubKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey priKey = (RSAPrivateKey) keyPair.getPrivate();
            privateKey = Base64.getEncoder().encode(priKey.getEncoded());
            publicKey = Base64.getEncoder().encode(pubKey.getEncoded());
            System.out.println("公钥：" + new String(publicKey));
            System.out.println("私钥：" + new String(privateKey));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("生成密钥对失败！");
        }
    }


    public static void main(String[] args) {
        KeyGenerator keyGenerator = new KeyGenerator();
        keyGenerator.generater();
    }

}
