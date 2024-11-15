# 数据加密demo

## 环境

- JDK 8
- MySQL 8.0.33

## 配置

- 创建本地数据库

```
create database db_sensitive_data default character set utf8;
use db_sensitive_data;
create table tbl_my_sensitive_data (
    id bigint auto_increment primary key,
    name varchar(255) not null comment '姓名 (非敏感数据)',
    tel varchar(255) not null comment '电话 (敏感数据)'
);
```

- 修改 `src/main/resources/application.yml` 中的数据库配置

## 测试

- 运行 `MySensitiveDataControllerTest` 测试用例
- 查看数据库 `db_sensitive_data` 中的表 `tbl_my_sensitive_data` 的数据是否被正确加密

```
> select * from tbl_my_sensitive_data;
+---------------------+------+------------------------------------+
| id                  | name | tel                                |
+---------------------+------+------------------------------------+
| 1856977779885400065 | name | SENSITIVE_FqCbky2fUBI/40RT1iSRlA== | << 13800138001的加密结果
| 1856977780179001346 | name | SENSITIVE_NeKoXkBB/cbm1eOvCYTb9A== | << 13800138000的加密结果
| 1856977780220944385 | name | SENSITIVE_NeKoXkBB/cbm1eOvCYTb9A== |
+---------------------+------+------------------------------------+
```