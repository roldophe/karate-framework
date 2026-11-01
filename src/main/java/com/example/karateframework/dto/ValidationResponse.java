package com.example.karateframework.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationResponse {
    private boolean valid;
    private String message;
}
