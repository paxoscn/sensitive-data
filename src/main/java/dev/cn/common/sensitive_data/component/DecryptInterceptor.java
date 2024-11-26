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
package dev.cn.common.sensitive_data.component;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;

import dev.cn.common.sensitive_data.annotation.SensitiveData;
import dev.cn.common.sensitive_data.annotation.SensitiveField;
import dev.cn.common.sensitive_data.support.ValueHelper;
import dev.cn.common.sensitive_data.util.CryptUtils;

/**
 * 这里是对找出来的字符串结果集进行解密所以是ResultSetHandler
 * args是指定预编译语句
 */
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class DecryptInterceptor implements Interceptor {

    @Value("${sensitive-data.data-crypt.enabled:false}")
    private boolean enabled;
    @Value("${sensitive-data.data-crypt.key-algorithm:AES}")
    private String keyAlgorithm;
    @Value("${sensitive-data.data-crypt.cipher-algorithm:AES/ECB/PKCS5Padding}")
    private String cipherAlgorithm;
    @Value("${sensitive-data.data-crypt.key:dummy-key}")
    private String key;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if(!enabled) {
            return invocation.proceed();
        }

        //取出查询的结果
        Object resultObject = invocation.proceed();
        if (Objects.isNull(resultObject)) {
            return null;
        }
        if (resultObject instanceof List) {
            //基于selectList
            List<?> resultList = (List<?>) resultObject;
            if (!resultList.isEmpty() && needToDecrypt(resultList.get(0))) {
                for (Object result : resultList) {
                    //逐一解密
                    decrypt(result);
                }
            }
        } else {
            //基于selectOne
            if (needToDecrypt(resultObject)) {
                decrypt(resultObject);
            }
        }
        return resultObject;
    }

    /**
     * 对单个结果集判空的一个方法
     * @param object
     * @return
     */
    private boolean needToDecrypt(Object object) {
        Class<?> objectClass = object.getClass();
        SensitiveData sensitiveData = AnnotationUtils.findAnnotation(objectClass, SensitiveData.class);
        return Objects.nonNull(sensitiveData);
    }

    /**
     * 将此过滤器加入到过滤器链当中
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    /**
     * 解密
     *
     * @param result
     * @param <T>
     * @return
     * @throws IllegalAccessException
     */
    private <T> T decrypt(T result) throws Exception {
        //取出resultType的类
        Class<?> resultClass = result.getClass();
        Field[] declaredFields = resultClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            //去除所有被EncryptDecryptField注解的字段
            SensitiveField sensitiveField = declaredField.getAnnotation(SensitiveField.class);
            if (!Objects.isNull(sensitiveField)) {
                //将此对象的 accessible 标志设置为指示的布尔值。值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。
                declaredField.setAccessible(true);
                //这里的result就相当于是字段的访问器
                Object object = declaredField.get(result);
                //只支持String解密
                if (object instanceof String) {
                    String value = (String) object;
                    //修改：没有标识则不解密
                    if(ValueHelper.isEncrypted(value)) {
                        value = ValueHelper.unprefixEncryptedValue(value);
                        value = CryptUtils.decrypt(value, key, keyAlgorithm, cipherAlgorithm);
                    }
                    //对注解在这段进行逐一解密
                    declaredField.set(result, value);
                }
            }
        }
        return result;
    }

}