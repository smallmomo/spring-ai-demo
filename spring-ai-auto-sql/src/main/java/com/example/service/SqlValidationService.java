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

    // 允许的WHERE条件关键词
    private static final List<String> ALLOWED_WHERE_KEYWORDS = Arrays.asList(
            "IS", "NOT", "NULL", "IN", "BETWEEN", "LIKE", "AND", "OR",
            "=", "!=", "<>", ">", "<", ">=", "<=", "0", "1", "DELETED");

    public boolean isValidSql(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        // 清理SQL内容，移除可能存在的```sql和```标记
        String cleanedSql = cleanSql(sql);
        String upperSql = cleanedSql.toUpperCase().trim();

        // 检查是否以SELECT开头
        if (!upperSql.startsWith("SELECT")) {
            return false;
        }

        // 检查是否包含危险关键词（仅在语句级别，不检查字段名）
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (upperSql.contains(keyword)) {
                // 特殊处理：如果是字段名中的关键词，允许通过
                if (isKeywordInFieldName(upperSql, keyword)) {
                    continue;
                }
                return false;
            }
        }

        // 检查是否包含分号（防止多语句）
        if (upperSql.contains(";")) {
            return false;
        }

        return true;
    }

    private String cleanSql(String sql) {
        if (sql == null) {
            return "";
        }

        String cleanedSql = sql.trim();

        // 移除开头的```sql标记
        if (cleanedSql.startsWith("```sql")) {
            cleanedSql = cleanedSql.substring(6).trim();
        }

        // 移除结尾的```标记
        if (cleanedSql.endsWith("```")) {
            cleanedSql = cleanedSql.substring(0, cleanedSql.length() - 3).trim();
        }

        // 移除开头的```标记
        if (cleanedSql.startsWith("```")) {
            cleanedSql = cleanedSql.substring(3).trim();
        }

        // 移除结尾的```标记
        if (cleanedSql.endsWith("```")) {
            cleanedSql = cleanedSql.substring(0, cleanedSql.length() - 3).trim();
        }

        return cleanedSql;
    }

    public String getValidationMessage(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return "SQL语句不能为空";
        }

        String upperSql = sql.toUpperCase().trim();

        if (!upperSql.startsWith("SELECT")) {
            return "只允许SELECT查询语句";
        }

        // 检查是否包含危险关键词（仅在语句级别，不检查字段名）
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (upperSql.contains(keyword)) {
                // 特殊处理：如果是DELETE关键词，检查是否为字段名
                if (keyword.equals("DELETE")) {
                    // 检查DELETE是否作为字段名使用
                    if (isDeleteAsFieldName(upperSql)) {
                        // 允许DELETED字段
                        continue;
                    }
                }
                return "SQL语句包含危险关键词: " + keyword;
            }
        }

        if (upperSql.contains(";")) {
            return "SQL语句不能包含分号";
        }

        // 检查WHERE子句中的关键词是否都是允许的
        if (upperSql.contains("WHERE")) {
            String whereClause = upperSql.substring(upperSql.indexOf("WHERE") + 6).trim();
            // 分割WHERE条件
            String[] conditions = whereClause.split("\\s+(AND|OR)\\s+");

            for (String condition : conditions) {
                // 移除括号
                condition = condition.replaceAll("[()]", "").trim();
                if (condition.isEmpty()) {
                    continue;
                }

                // 使用更精确的正则表达式分割条件，避免误判字段名
                // 匹配操作符 (=, !=, <>, >, <, >=, <=) 进行分割
                String[] parts = condition.split("\\s*(=|!=|<>|>=|<=|>|<)\\s*");

                for (String part : parts) {
                    part = part.trim();
                    if (part.isEmpty()) {
                        continue;
                    }

                    // 检查是否为危险关键词（仅检查完整的单词，避免误判字段名）
                    if (isDangerousKeyword(part)) {
                        return "SQL语句包含不允许的WHERE条件关键词: " + part;
                    }
                }
            }
        }

        return "SQL验证通过";
    }

    private boolean isDangerousKeyword(String part) {
        // 检查是否为危险关键词
        for (String keyword : DANGEROUS_KEYWORDS) {
            if (part.equalsIgnoreCase(keyword)) {
                return true;
            }
        }

        // 检查是否为不允许的WHERE关键词
        if (!ALLOWED_WHERE_KEYWORDS.contains(part.toUpperCase())) {
            // 额外检查：如果部分包含危险操作，则拒绝
            if (part.matches(
                    ".*\\b(DROP|DELETE|INSERT|UPDATE|ALTER|CREATE|TRUNCATE|EXEC|EXECUTE|xp_|sp_|GRANT|REVOKE|BACKUP|RESTORE|KILL|SHUTDOWN|LOAD_FILE|INTO OUTFILE)\\b.*")) {
                return true;
            }
            return true;
        }

        return false;
    }

    /**
     * 检查DELETE是否作为字段名使用
     * 
     * @return true如果DELETE仅作为字段名使用，false如果同时存在DELETE操作和字段名
     */
    private boolean isDeleteAsFieldName(String upperSql) {
        // 检查是否存在DELETE操作（危险操作）
        boolean hasDeleteOperation = upperSql.contains("DELETE ") && !upperSql.contains("DELETE FROM");

        // 检查是否存在删除字段
        boolean hasDeleteField = upperSql.contains(" IS_DELETED ") ||
                upperSql.contains(" IS_DELETE ") ||
                upperSql.contains(", DELETED ") ||
                upperSql.contains(" DELETED ") ||
                upperSql.contains("DELETED ") ||
                upperSql.contains(" DELETED");

        // 如果同时存在DELETE操作和删除字段，拒绝
        if (hasDeleteOperation && hasDeleteField) {
            return false;
        }

        // 如果只有删除字段，允许
        return hasDeleteField;
    }

    /**
     * 检查关键词是否作为字段名使用
     */
    private boolean isKeywordInFieldName(String upperSql, String keyword) {
        // 检查关键词是否作为字段名使用
        String[] fieldPatterns = {
                " " + keyword + " ", ", " + keyword + " ", " " + keyword + ",",
                " " + keyword + ";", " " + keyword + "\n", " " + keyword + "\r",
                keyword + " ", keyword + ",", keyword + ";", keyword + "\n", keyword + "\r"
        };

        // 检查是否在SELECT列表中
        if (upperSql.contains("SELECT") && upperSql.contains(keyword)) {
            String selectPart = upperSql.substring(upperSql.indexOf("SELECT") + 6,
                    upperSql.indexOf("FROM")).trim();
            if (selectPart.contains(keyword)) {
                return true;
            }
        }

        // 检查是否在字段名中
        for (String pattern : fieldPatterns) {
            if (upperSql.contains(pattern)) {
                return true;
            }
        }

        return false;
    }
}