# 网上购物系统 Java 项目

## 开发环境

- JDK 17 或更高版本，编译目标为 Java 17
- Maven 3.9，MySQL 8.4 LTS，MySQL Connector/J 9.7.0

通过 IntelliJ IDEA 打开本目录的 `pom.xml`，即可导入 Maven 项目。

## 配置和验证连接

首次配置时，从仓库根目录打开 PowerShell，将 [配置模板](.env.example) 复制为 Java 项目目录下的 `.env`。

```powershell
Set-Location -LiteralPath '05_JDBC应用编程'
Copy-Item -LiteralPath '.env.example' -Destination '.env'
```

编辑 `.env`，填写本机的数据库地址、账号和密码。模板里的 JDBC 地址可在创建业务库之前使用。`.env` 使用 UTF-8，每行一个 `KEY=VALUE`，以 `#` 开头的行是注释。值需要保留首尾空格时，可以用单引号或双引号包起来。配置优先读取 `.env`，缺少的项再从同名环境变量读取。`.env` 已由 Git 忽略。

在 Java 项目目录运行连库检查：

```powershell
mvn compile exec:exec
```

成功时会显示 `JDBC connection OK (SELECT 1 returned 1).`。在 IDEA 中直接运行 `com.shop.JdbcCheck` 时，将运行配置的工作目录设为 `05_JDBC应用编程`。

业务脚本统一放在仓库的 `04_数据库实现/`。脚本就绪后，每位成员在自己的 MySQL 实例上依次执行建表和初始化脚本，再把 `.env` 里的 `DB_URL` 改为包含实际数据库名的地址，例如 `jdbc:mysql://localhost:3306/shopping`。

## 分层

| Java 包 | 职责 |
| --- | --- |
| `com.shop.util` | `DBUtil.getConnection()` 创建 JDBC 连接，由调用方关闭。 |
| `com.shop.dao` | 各业务模块在这里实现 SQL 与数据读写。 |
| `com.shop.service` | 各业务模块在这里实现业务规则。跨多个 DAO 的操作由 Service 开启事务，并向 DAO 传入同一个连接，由 Service 统一提交或回滚。 |

`JdbcCheck` 是开发用的连库检查入口。后续网页接口可以调用同一组 Service 方法。
