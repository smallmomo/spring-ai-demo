package com.example.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class SqlValidationService {

    private static final List<String> DANGEROUS_KEYWORDS = Arrays.asList(
            "DROP", "DELETE", "INSERT", "UPDATE", "ALTER", "CREATE", "TRUNCATE",
            "EXEC", "EXECUTE", "xp_", "sp_", "GRANT", "REVOKE", "BACKUP",
            "RESTORE", "KILL", "SHUTDOWN", "LOAD_FILE", "INTO OUTFILE");

    public boolean isValidSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        // 转换为大写进行检查
        String upperSql = sql.toUpperCase().trim();

        // 检查是否以SELECT开头
        if (!upperSql.startsWith("SELECT")) {
            return false;
        }

        // 检查是否包含危险关键词
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (upperSql.contains(keyword)) {
                return false;
            }
        }

        // 检查是否包含分号（防止多语句）
        if (upperSql.contains(";")) {
            return false;
        }

        return true;
    }

    public String getValidationMessage(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "SQL语句不能为空";
        }

        String upperSql = sql.toUpperCase().trim();

        if (!upperSql.startsWith("SELECT")) {
            return "只允许SELECT查询语句";
        }

        for (String keyword : DANGEROUS_KEYWORDS) {
            if (upperSql.contains(keyword)) {
                return "SQL语句包含危险关键词: " + keyword;
            }
        }

        if (upperSql.contains(";")) {
            return "SQL语句不能包含分号";
        }

        return "SQL验证通过";
    }
}