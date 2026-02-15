package com.toryu.iims.ai.chat.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @Author: Aitenry
 * @Date: 2025/11/11 21:32
 * @Version: v1.0.0
 * @Description: AES/GCM 加密解密工具类
 **/
@Slf4j
@Component
public class AESEncryptionUtil {

    private static String secretKeyBase64;
    private static final int GCM_IV_LENGTH = 12; // 96 bits
    private static final int GCM_TAG_LENGTH = 16; // 128 bits

    public AESEncryptionUtil(@Value("${iims.encryption.master-key}") String masterKeyBase64) {
        AESEncryptionUtil.secretKeyBase64 = masterKeyBase64;
    }

    /**
     * 加密字符串
     * @param strToEncrypt 待加密的明文
     * @return Base64编码的 "IV:EncryptedData" 字符串
     */
    public static String encrypt(String strToEncrypt) {
        try {
            // 解码主密钥
            byte[] keyBytes = Base64.decodeBase64(secretKeyBase64);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // 生成随机 IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // 初始化 Cipher for Encryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv); // Tag length in bits
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, gcmSpec);

            // 4. 执行加密
            byte[] encryptedBytes = cipher.doFinal(strToEncrypt.getBytes());

            // 将 IV 和加密数据拼接，然后 Base64 编码
            // 格式: [IV][EncryptedData]
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.encodeBase64String(combined);
        } catch (Exception e) {
            log.error("Error while encrypting: {}", e.getMessage(), e);
            return null; // Or throw a custom exception
        }
    }

    /**
     * 解密字符串
     * @param strToDecrypt Base64编码的 "IV:EncryptedData" 字符串
     * @return 解密后的明文
     */
    public static String decrypt(String strToDecrypt) {
        try {
            // 解码输入字符串
            byte[] combined = Base64.decodeBase64(strToDecrypt);

            // 从组合数据中分离 IV 和加密数据
            if (combined.length < GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Encrypted data is too short to contain IV.");
            }
            byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH);
            byte[] encryptedData = Arrays.copyOfRange(combined, GCM_IV_LENGTH, combined.length);

            // 解码主密钥
            byte[] keyBytes = Base64.decodeBase64(secretKeyBase64);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");

            // 初始化 Cipher for Decryption
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv); // Tag length in bits
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, gcmSpec);

            // 执行解密
            byte[] decryptedBytes = cipher.doFinal(encryptedData);

            return new String(decryptedBytes);
        } catch (Exception e) {
            log.error("Error while decrypting: {}", e.getMessage(), e);
            return null; // Or throw a custom exception
        }
    }
}