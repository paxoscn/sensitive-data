# 敏感数据加解密工具

## 1. Background

当需要满足DSMM(数据安全能力成熟度)等规范的要求时, Java服务可能需要对数据库中的敏感数据, 如身份证号/手机号等, 进行加密存储, 在使用时再进行解密.

本项目是提供数据透明加解密的类库. 所谓透明加解密, 就是指Java应用写入数据库前的ORM实体属性值依旧是明文, 经过本类库的自动拦截处理, 实际写入数据库的则是密文. 同样的, 从数据库读取数据时, 本类库也会自动解密, 从而让Java应用获取到的实体属性值也是明文.

使用本类库, Java应用可以在不改动代码逻辑, 只增加几个注解的情况下, 即可实现对数据库中敏感数据的透明加解密.

**注: 当前工具版本暂时只支持MyBatis, 不支持JPA.**

## 2. Usage

### 2.1 使用透明加解密

项目实例在data-crypt-demo子目录中.

- 在项目中引入依赖

```xml
<dependency>
    <groupId>dev.cn</groupId>
    <artifactId>sensitive-data</artifactId>
    <version>1.0.0</version>
</dependency>
```

- 在SpringBoot启动类上添加@EnableTransparentCrypt注解

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import dev.cn.sensitive_data.annotation.EnableTransparentCrypt;

// 启用透明加解密
@EnableTransparentCrypt
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

- 在需要透明加解密的实体及属性上增加注解@SensitiveData和@SensitiveField. 非敏感数据不用添加注解.

```java
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import dev.cn.sensitive_data.annotation.SensitiveData;
import dev.cn.sensitive_data.annotation.SensitiveField;

import lombok.Data;

// 实体类中包含敏感数据
@SensitiveData
@Data
@TableName("tbl_my_sensitive_data")
public class MySensitiveDataPO {
    
    @TableId
    private Long id;
    // 非敏感数据不用添加注解
    private String name;
    // 敏感数据添加注解
    @SensitiveField
    private String tel;
}
```

- 在配置中加入敏感数据加密的秘钥并启用

```yaml
sensitive-data:
  data-crypt:
    # 启用透明加解密
    enabled: true
    # 秘钥算法
    key-algorithm: AES
    # 加密算法
    cipher-algorithm: AES/ECB/PKCS5Padding
    # 秘钥
    key: my-secret-key-1234567890
```

- 启动服务并验证在应用中读写的数据均为明文, 而数据库中的敏感数据均为以'SENSITIVE_'开头的密文.

### 2.2 手动加解密

若不想使用透明加解密, 而是想手动完成加解密逻辑, 可以执行CryptUtils工具类中的相关方法

### 2.3 明文数据迁移

- 对于存量的明文历史数据, 在数据库中可执行以下参考SQL, 将明文数据加密为密文数据. **执行前必须做好备份**

(假设'tbl_my_sensitive_data'为包含敏感数据的表, 'tel'为敏感数据列, 'my-secret-key-1234567890'为秘钥)

```sql
UPDATE tbl_my_sensitive_data
SET tel = CONCAT('SENSITIVE_', TO_BASE64(AES_ENCRYPT(tel, UNHEX(SUBSTR(SHA2('my-secret-key-1234567890', 512), 1, 32)))))
WHERE tel NOT LIKE 'SENSITIVE_%';
```

# 3 Support

[unrealwalker@126.com](mailto:unrealwalker@126.com)

# 4 Roadmap

* 2024-12-31 1.1.0 支持JPA
* 2024-11-15 1.0.0 支持MyBatis

# 5 Contributing

请联系: [unrealwalker@126.com](mailto:unrealwalker@126.com)

# 6 Authors and Acknowledgment

[unrealwalker@126.com](mailto:unrealwalker@126.com)

感谢: [几孤风月丶](https://blog.csdn.net/qq_45454294?type=blog) [Mybatis拦截器优雅的实现敏感数据的加解密](https://blog.csdn.net/qq_45454294/article/details/122012444)

# 7 License

MIT

# 8 Project Status

1.0.0 已发布