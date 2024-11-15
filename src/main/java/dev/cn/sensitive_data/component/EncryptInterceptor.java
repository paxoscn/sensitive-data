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
package dev.cn.sensitive_data.component;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;

import dev.cn.sensitive_data.annotation.SensitiveData;
import dev.cn.sensitive_data.annotation.SensitiveField;
import dev.cn.sensitive_data.support.Constants;
import dev.cn.sensitive_data.util.CryptUtils;

/**
 * @Intercepts注解开启拦截器
 * type 属性指定当前拦截器使用StatementHandler 、ResultSetHandler、ParameterHandler，Executor的一种
 * method 属性指定使用以上四种类型的具体方法（可进入class内部查看其方法）。
 * args 属性指定预编译语句
 */
@Intercepts({
        //@Signature注解定义拦截器的实际类型
        @Signature(type = ParameterHandler.class, method = "setParameters", args = PreparedStatement.class),
})
public class EncryptInterceptor implements Interceptor {

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

        //@Signature 指定了 type= parameterHandler 后，这里的 invocation.getTarget() 便是parameterHandler
        //若指定ResultSetHandler ，这里则能强转为ResultSetHandler
        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
        //获取参数对象，即mapper中paramsType的实例
        Field paramsField = parameterHandler.getClass().getDeclaredField("parameterObject");
        //将此对象的 accessible 标志设置为指示的布尔值。值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。
        paramsField.setAccessible(true);
        //取出实例
        Object parameterObject = paramsField.get(parameterHandler);
        if (parameterObject != null) {
            if (parameterObject instanceof ParamMap) {
                ParamMap<?> paramMap = (ParamMap<?>) parameterObject;
                for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                    if (entry.getValue() != null) {
                        tryEncrypting(entry.getValue());
                    }
                }
            } else {
                tryEncrypting(parameterObject);
            }
        }
        //获取原方法的返回值
        return invocation.proceed();
    }

    /**
     * 一定要配置，加入此拦截器到拦截器链
     * @param target
     * @return
     */
    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private void tryEncrypting(Object parameterObject) throws Exception {
        Class<?> parameterObjectClass = parameterObject.getClass();
        //校验该实例的类是否被@SensitiveData所注解
        SensitiveData sensitiveData = AnnotationUtils.findAnnotation(parameterObjectClass, SensitiveData.class);
        if (Objects.nonNull(sensitiveData)) {
            //取出当前类的所有字段，传入加密方法
            Field[] declaredFields = parameterObjectClass.getDeclaredFields();
            encrypt(declaredFields, parameterObject);
        }
    }

    /**
     *
     * @param aesFields paramsObject所申明的字段
     * @param paramsObject mapper中paramsType的实例
     * @param <T>
     * @return
     * @throws IllegalAccessException 字段不可访问的异常
     */
    private <T> T encrypt(Field[] aesFields, T paramsObject) throws Exception {
        for (Field aesField : aesFields) {
            //取出所有被EncryptDecryptField注解的字段
            SensitiveField Field = aesField.getAnnotation(SensitiveField.class);
            if (!Objects.isNull(Field)) {
                //将此对象的 accessible 标志设置为指示的布尔值。值为 true 则指示反射的对象在使用时应该取消 Java 语言访问检查。
                aesField.setAccessible(true);
                Object object = aesField.get(paramsObject);
                //这里暂时只对String类型来加密
                if (object instanceof String) {
                    String value = (String) object;
                    String encrypt = value;
                    //修改: 如果有标识则不加密，没有则加密并加上标识前缀
                    if(!value.startsWith(Constants.KEY_SENSITIVE)) {
                        encrypt = CryptUtils.encrypt(value, key, keyAlgorithm, cipherAlgorithm);
                        encrypt = Constants.KEY_SENSITIVE + encrypt;
                    }
                    //开始对字段加密使用自定义的AES加密工具
                    aesField.set(paramsObject, encrypt);
                }
            }
        }
        return paramsObject;
    }

}