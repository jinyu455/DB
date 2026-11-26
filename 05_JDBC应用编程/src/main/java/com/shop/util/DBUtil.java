package com.shop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {
    private DBUtil() {
    }

    /**
     * 创建数据库连接，调用方负责关闭。
     */
    public static Connection getConnection() throws SQLException {
        String url = requireConfiguration("DB_URL");
        String user = requireConfiguration("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            throw new IllegalStateException("请设置环境变量 DB_PASSWORD。");
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String requireConfiguration(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("请设置环境变量 " + name + "。");
        }
        return value;
    }
}
