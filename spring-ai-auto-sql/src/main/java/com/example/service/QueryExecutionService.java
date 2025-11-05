package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QueryExecutionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> executeQuery(String sql, Object[] parameters) {
        long startTime = System.currentTimeMillis();

        try {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, parameters);
            long endTime = System.currentTimeMillis();

            // 添加执行时间到结果中
            result.add(0, Map.of(
                    "execution_time", (endTime - startTime) + "ms",
                    "row_count", result.size() - 1));

            return result;
        } catch (Exception e) {
            throw new RuntimeException("查询执行失败: " + e.getMessage(), e);
        }
    }
}