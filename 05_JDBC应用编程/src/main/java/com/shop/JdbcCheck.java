package com.shop;

import com.shop.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class JdbcCheck {
    private JdbcCheck() {
    }

    public static void main(String[] args) throws SQLException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet result = statement.executeQuery()) {
            if (!result.next() || result.getInt(1) != 1) {
                throw new SQLException("JDBC 检查失败，SELECT 1 未返回预期值。");
            }
            System.out.println("JDBC connection OK (SELECT 1 returned 1).");
        }
    }
}
