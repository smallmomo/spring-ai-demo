package com.example.dto;

import lombok.Data;

@Data
public class QueryRequest {
    private String question;
    private String databaseSchema;
}