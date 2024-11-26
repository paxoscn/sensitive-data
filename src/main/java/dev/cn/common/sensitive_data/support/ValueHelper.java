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
package dev.cn.common.sensitive_data.support;

/**
 * 值处理工具类
 * 
 * @since 1.1.0
 */
public class ValueHelper {

    /**
     * 根据是否有前缀判断是否是加密过的值
     * 
     * @since 1.1.0
     * @param value 需要判断的值
     * @return 是否是加密过的值
     */
    public static boolean isEncrypted(String value) {
        return value.startsWith(Constants.KEY_SENSITIVE);
    }

    /**
     * 给加密过的值加上前缀
     * 
     * @since 1.1.0
     * @param encryptedValue 加密过的值
     * @return 加上前缀后的值
     */
    public static String prefixEncryptedValue(String encryptedValue) {
        return Constants.KEY_SENSITIVE + encryptedValue;
    }

    /**
     * 去掉加密过的值的前缀
     * 
     * @since 1.1.0
     * @param prefixedEncryptedValue 带有前缀的加密过的值
     * @return 去掉前缀后的值
     */
    public static String unprefixEncryptedValue(String prefixedEncryptedValue) {
        return prefixedEncryptedValue.substring(Constants.KEY_SENSITIVE.length());
    }
}