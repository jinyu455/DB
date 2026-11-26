# 网上购物系统 Java 项目

## 开发环境

- JDK 17 或更高版本，编译目标为 Java 17
- Maven 3.9，MySQL 8.4 LTS，MySQL Connector/J 9.7.0

通过 IntelliJ IDEA 打开本目录的 `pom.xml`，即可导入 Maven 项目。

## 配置和验证连接

从仓库根目录打开 PowerShell。下面的地址只连接 MySQL 实例，可以在创建业务库之前运行。账号和密码请填本机的配置。

```powershell
Set-Location -LiteralPath '05_JDBC应用编程'
$env:DB_URL = 'jdbc:mysql://localhost:3306/'
$env:DB_USER = '你的本机数据库账号'
$env:DB_PASSWORD = '你的本机数据库密码'
mvn compile exec:exec
```

成功时会显示 `JDBC connection OK (SELECT 1 returned 1).`。也可在 IDEA 的运行配置里设置这三个环境变量，再运行 `com.shop.JdbcCheck`。

业务脚本统一放在仓库的 `04_数据库实现/`。脚本就绪后，每位成员在自己的 MySQL 实例上依次执行建表和初始化脚本，再把 `DB_URL` 改为包含实际数据库名的地址，例如 `jdbc:mysql://localhost:3306/shopping`。账号和密码由各成员在本机配置。

## 分层

| Java 包 | 职责 |
| --- | --- |
| `com.shop.util` | `DBUtil.getConnection()` 创建 JDBC 连接，由调用方关闭。 |
| `com.shop.dao` | 各业务模块在这里实现 SQL 与数据读写。 |
| `com.shop.service` | 各业务模块在这里实现业务规则。跨多个 DAO 的操作由 Service 开启事务，并向 DAO 传入同一个连接，由 Service 统一提交或回滚。 |

`JdbcCheck` 是开发用的连库检查入口。后续网页接口可以调用同一组 Service 方法。
