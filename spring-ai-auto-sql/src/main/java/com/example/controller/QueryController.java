package com.example.controller;

import com.example.dto.QueryRequest;
import com.example.dto.QueryResponse;
import com.example.model.TableInfo;
import com.example.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
public class QueryController {

    @Autowired
    private DatabaseMetadataService databaseMetadataService;

    @Autowired
    private PromptBuilder promptBuilder;

    @Autowired
    private SqlGenerationService sqlGenerationService;

    @Autowired
    private SqlValidationService sqlValidationService;

    @Autowired
    private QueryExecutionService queryExecutionService;

    @PostMapping("/query")
    public ResponseEntity<QueryResponse> query(@RequestBody QueryRequest request) {
        try {
            // 1. 获取数据库元数据
            List<TableInfo> tableInfos = databaseMetadataService.getAllTables();

            // 2. 构造Prompt
            String prompt = promptBuilder.buildPrompt(request.getQuestion(), tableInfos);

            // 3. 生成SQL
            String generatedSql = sqlGenerationService.generateSql(prompt);

            // 4. 验证SQL
            if (!sqlValidationService.isValidSql(generatedSql)) {
                log.error("SQL生成失败: {}", generatedSql);
                String validationMessage = sqlValidationService.getValidationMessage(generatedSql);
                return ResponseEntity.ok(QueryResponse.error("SQL生成失败: " + validationMessage));
            }

            // 5. 执行查询
            List<Object[]> parameters = extractParameters(generatedSql); // 简化实现，实际需要更复杂的参数提取
            Object[] paramArray = parameters.isEmpty() ? new Object[0] : parameters.get(0);

            List result = queryExecutionService.executeQuery(generatedSql, paramArray);

            // 6. 返回结果
            return ResponseEntity.ok(QueryResponse.success(generatedSql, paramArray, result));

        } catch (Exception e) {
            return ResponseEntity.ok(QueryResponse.error("处理请求时发生错误: " + e.getMessage()));
        }
    }

    @GetMapping("/schema")
    public ResponseEntity<List<TableInfo>> getSchema() {
        try {
            List<TableInfo> schema = databaseMetadataService.getAllTables();
            return ResponseEntity.ok(schema);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 简化的参数提取方法，实际实现需要更复杂的解析
    private List<Object[]> extractParameters(String sql) {
        // 这里应该实现SQL参数提取逻辑
        // 目前返回空列表，表示没有参数
        return List.of();
    }
}