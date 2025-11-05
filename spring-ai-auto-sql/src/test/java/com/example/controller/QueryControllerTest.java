package com.example.controller;

import com.example.dto.QueryRequest;
import com.example.service.DatabaseMetadataService;
import com.example.service.PromptBuilder;
import com.example.service.SqlGenerationService;
import com.example.service.SqlValidationService;
import com.example.service.QueryExecutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class QueryControllerTest {

    @Autowired
    private QueryController queryController;

    @MockBean
    private DatabaseMetadataService databaseMetadataService;

    @MockBean
    private PromptBuilder promptBuilder;

    @MockBean
    private SqlGenerationService sqlGenerationService;

    @MockBean
    private SqlValidationService sqlValidationService;

    @MockBean
    private QueryExecutionService queryExecutionService;

    @Test
    public void testQueryEndpoint() {
        // 准备测试数据
        QueryRequest request = new QueryRequest();
        request.setQuestion("查找所有用户");

        // 模拟数据库元数据
        when(databaseMetadataService.getAllTables()).thenReturn(List.of());

        // 模拟Prompt构建
        when(promptBuilder.buildPrompt(any(), any())).thenReturn("测试Prompt");

        // 模拟SQL生成
        when(sqlGenerationService.generateSql(any())).thenReturn("SELECT * FROM users");

        // 模拟SQL验证
        when(sqlValidationService.isValidSql(any())).thenReturn(true);
        when(sqlValidationService.getValidationMessage(any())).thenReturn("SQL验证通过");

        // 模拟查询执行
        when(queryExecutionService.executeQuery(any(), any())).thenReturn(List.of(
                Map.of("execution_time", "100ms", "row_count", 1),
                Map.of("id", 1, "name", "张三")));

        // 调用接口
        ResponseEntity response = queryController.query(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testInvalidSqlGeneration() {
        // 准备测试数据
        QueryRequest request = new QueryRequest();
        request.setQuestion("删除所有用户");

        // 模拟数据库元数据
        when(databaseMetadataService.getAllTables()).thenReturn(List.of());

        // 模拟Prompt构建
        when(promptBuilder.buildPrompt(any(), any())).thenReturn("测试Prompt");

        // 模拟SQL生成
        when(sqlGenerationService.generateSql(any())).thenReturn("DELETE FROM users");

        // 模拟SQL验证
        when(sqlValidationService.isValidSql(any())).thenReturn(false);
        when(sqlValidationService.getValidationMessage(any())).thenReturn("只允许SELECT查询语句");

        // 调用接口
        ResponseEntity response = queryController.query(request);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }
}