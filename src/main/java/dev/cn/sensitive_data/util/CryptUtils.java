/*
 * MIT License
 * 
 * Copyright (c) 2024 Mergen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.cn.sensitive_data.util;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtils {

    private static final String SHA_512 = "SHA-512";

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param key 秘钥
     * @param keyAlgorithm 秘钥算法
     * @param cipherAlgorithm 加密算法
     * @return 返回Base64转码后的加密数据
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String encrypt(String content, String key, String keyAlgorithm, String cipherAlgorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);// 创建密码器

        byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);

        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key, keyAlgorithm));// 初始化为加密模式的密码器

        byte[] result = cipher.doFinal(byteContent);// 加密

        // Base64是一种基于64个可打印字符来表示二进制数据的表示方法。
        return Base64.getEncoder().encodeToString(result);// 通过Base64转码返回
    }

    /**
     * AES 解密操作
     *
     * @param content 待解密内容
     * @param key 秘钥
     * @param keyAlgorithm 秘钥算法
     * @param cipherAlgorithm 加密算法
     * @return 返回解密后的内容
     * @throws NoSuchPaddingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public static String decrypt(String content, String key, String keyAlgorithm, String cipherAlgorithm)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException {
        // 实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);

        // 使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key, keyAlgorithm));

        // 执行操作
        // Base64是一种基于64个可打印字符来表示二进制数据的表示方法。
        byte[] result = cipher.doFinal(Base64.getDecoder().decode(content));

        return new String(result, StandardCharsets.UTF_8);
    }

    /**
     * 生成加密秘钥
     *
     * @param key 秘钥
     * @param keyAlgorithm 秘钥算法
     * @return 返回加密秘钥
     */
    public static Key getSecretKey(String key, String keyAlgorithm) throws NoSuchAlgorithmException {
        if (key == null) {
            throw new IllegalArgumentException("密钥不能为null");
        }

        // 确保算法存在. 如果算法不存在, 该行会抛出 NoSuchAlgorithmException 异常.
        KeyGenerator.getInstance(keyAlgorithm);
        
        // 使用SHA-512对密钥进行哈希处理
        MessageDigest digest = MessageDigest.getInstance(SHA_512);
        byte[] hashedKey = digest.digest(key.getBytes(StandardCharsets.UTF_8));
        
        return new SecretKeySpec(hashedKey, 0, 16, keyAlgorithm);// 转换为AES专用密钥
    }
}