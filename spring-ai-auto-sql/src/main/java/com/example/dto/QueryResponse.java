package com.example.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryResponse {
    private boolean success;
    private String sql;
    private Object[] parameters;
    private List<Map<String, Object>> result;
    private String message;
    private String executionTime;

    // 成功响应的构造方法
    public static QueryResponse success(String sql, Object[] parameters, List<Map<String, Object>> result) {
        QueryResponse response = new QueryResponse();
        response.setSuccess(true);
        response.setSql(sql);
        response.setParameters(parameters);
        response.setResult(result);
        response.setMessage("查询成功");

        if (result != null && !result.isEmpty()) {
            Map<String, Object> meta = result.get(0);
            response.setExecutionTime((String) meta.get("execution_time"));
        }

        return response;
    }

    // 错误响应的构造方法
    public static QueryResponse error(String message) {
        QueryResponse response = new QueryResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}