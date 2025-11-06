package com.example.service;

import com.example.model.TableInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptBuilder {

    public String buildPrompt(String userQuestion, List<TableInfo> tableInfos) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("系统角色：您是一个专业的SQL查询助手，根据用户问题生成准确的SQL查询语句。\n\n");

        prompt.append("数据库信息：\n");
        for (TableInfo table : tableInfos) {
            prompt.append(String.format("表名：%s\n", table.getTableName()));
            if (table.getTableComment() != null && !table.getTableComment().isEmpty()) {
                prompt.append(String.format("表说明：%s\n", table.getTableComment()));
            }
            prompt.append("字段：\n");
            for (TableInfo.ColumnInfo column : table.getColumns()) {
                prompt.append(String.format("- %s (%s", column.getColumnName(), column.getDataType()));
                if (column.isPrimaryKey()) {
                    prompt.append(", 主键");
                }
                if (!column.isNullable()) {
                    prompt.append(", 非空");
                }
                if (column.getColumnComment() != null && !column.getColumnComment().isEmpty()) {
                    prompt.append(", ").append(column.getColumnComment());
                }
                prompt.append(")\n");
            }
            prompt.append("\n");
        }

        prompt.append(String.format("用户问题：%s\n\n", userQuestion));

        prompt.append("要求：\n");
        prompt.append("1. 只生成SELECT查询语句，不允许使用INSERT、UPDATE、DELETE、DROP等操作\n");
        prompt.append("2. 使用参数化查询，避免SQL注入\n");
        prompt.append("3. 基于提供的数据库结构生成准确的SQL\n");
        prompt.append("4. 如果问题无法用现有表结构回答，请说明原因\n");
        prompt.append("5. 严格只返回SQL语句，不要包含任何额外的解释、符号、标记、代码块或格式标记\n");
        prompt.append("6. 不要使用```、```sql、markdown代码块等任何格式化标记\n");
        prompt.append("7. 不要包含任何说明文字，只返回SQL语句本身\n");
        prompt.append("8. 确保SQL语句可以直接执行，不要包含任何非SQL内容\n\n");

        prompt.append("请严格按照上述要求生成SQL查询语句，只返回纯SQL语句：");

        return prompt.toString();
    }
}