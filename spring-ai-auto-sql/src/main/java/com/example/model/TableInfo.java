package com.example.model;

import lombok.Data;
import java.util.List;

@Data
public class TableInfo {
    private String tableName;
    private String tableComment;
    private List<ColumnInfo> columns;

    @Data
    public static class ColumnInfo {
        private String columnName;
        private String dataType;
        private String columnComment;
        private boolean isPrimaryKey;
        private boolean isNullable;
    }
}