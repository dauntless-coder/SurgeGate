package com.surgegate.backend.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDto {
    private String error;
    // Optional: Add a second constructor to handle simple string messages easily
    public ErrorDto(String prefix, String message) {
        this.error = prefix + ": " + message;
    }
}