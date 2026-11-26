package com.shop.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class DBUtil {
    private DBUtil() {
    }

    /**
     * 创建数据库连接，调用方负责关闭。
     */
    public static Connection getConnection() throws SQLException {
        Map<String, String> settings = readDotEnv();
        String url = requireConfiguration(settings, "DB_URL");
        String user = requireConfiguration(settings, "DB_USER");
        String password = requireConfiguration(settings, "DB_PASSWORD");
        return DriverManager.getConnection(url, user, password);
    }

    private static String requireConfiguration(Map<String, String> settings, String name) {
        String value = settings.getOrDefault(name, System.getenv(name));
        if (value == null || (value.isBlank() && !name.equals("DB_PASSWORD"))) {
            throw new IllegalStateException("请在 .env 或环境变量中设置 " + name + "。");
        }
        return value;
    }

    private static Map<String, String> readDotEnv() {
        String contents;
        try {
            contents = Files.readString(Path.of(".env"));
        } catch (NoSuchFileException e) {
            return Map.of();
        } catch (IOException e) {
            throw new IllegalStateException("无法读取 .env 文件。", e);
        }

        if (contents.startsWith("\uFEFF")) {
            contents = contents.substring(1);
        }
        Map<String, String> settings = new HashMap<>();
        for (String line : contents.split("\\R")) {
            String entry = line.strip();
            if (entry.isEmpty() || entry.startsWith("#")) {
                continue;
            }

            String[] pair = entry.split("=", 2);
            if (pair.length != 2 || pair[0].isBlank()) {
                throw new IllegalStateException(".env 的配置格式应为 KEY=VALUE。");
            }
            String value = pair[1].strip();
            if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\""))
                    || (value.startsWith("'") && value.endsWith("'")))) {
                value = value.substring(1, value.length() - 1);
            }
            settings.put(pair[0].strip(), value);
        }
        return settings;
    }
}
