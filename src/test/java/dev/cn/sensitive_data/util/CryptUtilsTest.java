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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class CryptUtilsTest {

    private static final String KEY = "test-key-123456";
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    
    @Test
    public void testEncryptAndDecrypt() throws Exception {
        // 准备测试数据
        String originalContent = "Hello, World!";
        
        // 测试加密
        String encryptedContent = CryptUtils.encrypt(
            originalContent, 
            KEY, 
            KEY_ALGORITHM, 
            CIPHER_ALGORITHM
        );
        
        // 确保加密后的内容与原始内容不同
        assertNotEquals(originalContent, encryptedContent);
        
        // 测试解密
        String decryptedContent = CryptUtils.decrypt(
            encryptedContent, 
            KEY, 
            KEY_ALGORITHM, 
            CIPHER_ALGORITHM
        );
        
        // 验证解密后的内容与原始内容相同
        assertEquals(originalContent, decryptedContent);
    }
    
    @Test
    public void testEncryptWithEmptyContent() throws Exception {
        String emptyContent = "";
        
        String encryptedContent = CryptUtils.encrypt(
            emptyContent, 
            KEY, 
            KEY_ALGORITHM, 
            CIPHER_ALGORITHM
        );
        
        String decryptedContent = CryptUtils.decrypt(
            encryptedContent, 
            KEY, 
            KEY_ALGORITHM, 
            CIPHER_ALGORITHM
        );
        
        assertEquals(emptyContent, decryptedContent);
    }
    
    @Test
    public void testEncryptWithNullKey() {
        String content = "Test content";
        
        assertThrows(IllegalArgumentException.class, () -> {
            CryptUtils.encrypt(content, null, KEY_ALGORITHM, CIPHER_ALGORITHM);
        });
    }
    
    @Test
    public void testEncryptWithSpecialCharacters() throws Exception {
        String specialContent = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        String encryptedContent = CryptUtils.encrypt(
            specialContent, 
            KEY, 
            KEY_ALGORITHM, 
            CIPHER_ALGORITHM
        );
        
        String decryptedContent = CryptUtils.decrypt(
            encryptedContent, 
            KEY, 
            KEY_ALGORITHM, 
            CIPHER_ALGORITHM
        );
        
        assertEquals(specialContent, decryptedContent);
    }
    
    @Test
    void getSecretKey_ShouldGenerateValidKey() throws NoSuchAlgorithmException {
        // 当使用有效参数时
        Key key = CryptUtils.getSecretKey(KEY, KEY_ALGORITHM);
        
        // 验证生成的密钥
        assertNotNull(key, "生成的密钥不应为null");
        assertEquals(KEY_ALGORITHM, key.getAlgorithm(), "密钥算法应该是AES");
        assertEquals(16, key.getEncoded().length, "AES密钥长度应该是128位(16字节)");
    }
    
    @Test
    void getSecretKey_ShouldGenerateConsistentKeys() throws NoSuchAlgorithmException {
        // 使用相同的输入参数多次生成密钥
        Key key1 = CryptUtils.getSecretKey(KEY, KEY_ALGORITHM);
        Key key2 = CryptUtils.getSecretKey(KEY, KEY_ALGORITHM);
        
        // 验证生成的密钥是否一致
        assertArrayEquals(key1.getEncoded(), key2.getEncoded(), 
            "使用相同输入参数生成的密钥应该相同");
    }
    
    @Test
    void getSecretKey_ShouldThrowException_WhenKeyIsNull() {
        // 验证空密钥是否抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> CryptUtils.getSecretKey(null, KEY_ALGORITHM));
        
        assertEquals("密钥不能为null", exception.getMessage());
    }
    
    @Test
    void getSecretKey_ShouldThrowException_WhenAlgorithmInvalid() {
        // 验证无效算法是否抛出异常
        assertThrows(NoSuchAlgorithmException.class, 
            () -> CryptUtils.getSecretKey(KEY, "InvalidAlgorithm"));
    }
} 