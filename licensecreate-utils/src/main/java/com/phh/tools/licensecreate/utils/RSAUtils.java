package com.phh.tools.licensecreate.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author penghao
 * @Description 对AES密码加密
 * @createDate 2022/05/05
 * @createTime 14:28
 */
public class RSAUtils {

    /**
     * 公钥base64
     */
    private static String puk = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdfujgTmG4aOa4oK2VysmKvAI+hurN/wuKQjzgJTo3ct6TH5NHFHncb9KXijC1xk2Po+pJ8UjU4XGjU4gq5yhTdeSYPYR6hj5jqLy8fkWpFzeC6RvM4bLDe1lDNKphpcUoo5ZO7T77w9fX2lgJSyy/8LxdBThc4Megga3KW1/W4wIDAQAB";

    /**
     * 加密
     *
     * @return
     * @throws Exception
     */
    protected static String encrypt(String content) throws Exception {

        byte[] publicKeyBytes = puk.getBytes();
        X509EncodedKeySpec x = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBytes));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(x);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        byte[] result = cipher.doFinal(content.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 私钥base64
     */
    private static String prk = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJ1+6OBOYbho5rigrZXKyYq8Aj6G6s3/C4pCPOAlOjdy3pMfk0cUedxv0peKMLXGTY+j6knxSNThcaNTiCrnKFN15Jg9hHqGPmOovLx+RakXN4LpG8zhssN7WUM0qmGlxSijlk7tPvvD19faWAlLLL/wvF0FOFzgx6CBrcpbX9bjAgMBAAECgYA8uRWohg//PdLXFHxY6JrUNrDW0sXtLoyQfgFimnfbsRpHt0DdgvOJHkQf0VP+gbqdyyEl6TWfflyGEErL39wX1rrosy+LpiN0HeISERJuwJtuiGeR+0qw+Xz2M7VE+e5oD94dRtlzERft2mcDbQAQYUCFNgUBtd1dCJgMJPZJYQJBANHxKKHqMbsH91JsGP8eCu+yeMah0X8cT79nwD71SJRc03W5P1MPKhRyGWJj0M+Wax32pAPCMTfbj19scLplJpUCQQDADD5OuSLYRVqx68/CYbFVK3ye/YD4Cgc+0kT9SoI9bLB10JumHT0seDGeXQqwUPAF3bBZGI8pW2bdtzDj8YGXAkABQXgEv+ncPIf2Lj9YB035cQ/X4E/oerrfYjd8KOtuN7/sDFecn5KY3LXaKM6u7y9k1nzUqOyycNXCtFtYQhKhAkBvgyxyvaFz/uFoyko6zksP705Pa1eFrx0B50pT4P26+O+FmXmnfPbWaXw2PkREmNqmLVGGinImS4JxXzuuP79FAkAFQejjE+5Twi8oSCcNwse7FFP86U6jgcc+S+XCUUkLXQ5SPlkyb037hwoV1lEEJpcyI2tSFRxBKT89KZN0Nfat";

    protected static String decrypt(String signEncrypt) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] privateKeyBytes = prk.getBytes();
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBytes));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        byte[] result = cipher.doFinal(Base64.getDecoder().decode(signEncrypt));
        return new String(result);
    }

    public static void main(String[] args) throws Exception {
        String password = "123456";
        String a = encrypt(password);
        System.out.println("AES加密秘钥：" + a);
    }
}
