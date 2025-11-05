package com.example.service;

import com.example.mapper.TableMetadataMapper;
import com.example.model.TableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseMetadataService {

    @Autowired
    private TableMetadataMapper tableMetadataMapper;

    public List<TableInfo> getAllTables() {
        List<TableInfo> tables = new ArrayList<>();

        // 获取所有表
        List<Map<String, Object>> tablesList = tableMetadataMapper.getAllTables();

        for (Map<String, Object> tableMap : tablesList) {
            String tableName = (String) tableMap.get("TABLE_NAME");
            String tableComment = (String) tableMap.get("TABLE_COMMENT");

            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName(tableName);
            tableInfo.setTableComment(tableComment);
            tableInfo.setColumns(getTableColumns(tableName));

            tables.add(tableInfo);
        }

        return tables;
    }

    private List<TableInfo.ColumnInfo> getTableColumns(String tableName) {
        List<TableInfo.ColumnInfo> columns = new ArrayList<>();

        // 获取列信息
        List<Map<String, Object>> columnsList = tableMetadataMapper.getTableColumns(tableName);

        // 获取主键信息
        List<String> primaryKeysList = tableMetadataMapper.getTablePrimaryKeys(tableName);

        for (Map<String, Object> columnMap : columnsList) {
            String columnName = (String) columnMap.get("COLUMN_NAME");
            String dataType = (String) columnMap.get("DATA_TYPE");
            String columnComment = (String) columnMap.get("COLUMN_COMMENT");
            String isNullable = (String) columnMap.get("IS_NULLABLE");
            String columnKey = (String) columnMap.get("COLUMN_KEY");

            TableInfo.ColumnInfo column = new TableInfo.ColumnInfo();
            column.setColumnName(columnName);
            column.setDataType(dataType);
            column.setColumnComment(columnComment);
            column.setPrimaryKey("PRI".equals(columnKey));
            column.setNullable("YES".equals(isNullable));

            columns.add(column);
        }

        return columns;
    }
}