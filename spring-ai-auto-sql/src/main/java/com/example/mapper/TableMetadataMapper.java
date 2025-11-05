package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface TableMetadataMapper extends BaseMapper<Map<String, Object>> {

    /**
     * 获取所有表名
     */
    @Select("SELECT TABLE_NAME, TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = DATABASE()")
    List<Map<String, Object>> getAllTables();

    /**
     * 获取指定表的列信息
     */
    @Select("SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT, IS_NULLABLE, COLUMN_KEY " +
            "FROM INFORMATION_SCHEMA.COLUMNS " +
            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = #{tableName}")
    List<Map<String, Object>> getTableColumns(String tableName);

    /**
     * 获取指定表的主键信息
     */
    @Select("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE " +
            "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = #{tableName} AND CONSTRAINT_NAME = 'PRIMARY'")
    List<String> getTablePrimaryKeys(String tableName);
}