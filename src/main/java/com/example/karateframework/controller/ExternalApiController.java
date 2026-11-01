package com.example.karateframework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.karateframework.dto.ExternalUserResponse;
import com.example.karateframework.dto.ValidationRequest;
import com.example.karateframework.dto.ValidationResponse;
import com.example.karateframework.exception.ExternalApiException;
import com.example.karateframework.service.ExternalApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/external-integration")
@RequiredArgsConstructor
@Slf4j
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<ExternalUserResponse> getExternalUser(@PathVariable String userId) {
        try {
            ExternalUserResponse response = externalApiService.getExternalUser(userId);
            return ResponseEntity.ok(response);
        } catch (ExternalApiException e) {
            if (log.isErrorEnabled()) {
                log.error(
                        "Failed to fetch external user {}: {}",
                        sanitizeForLog(userId),
                        e.getMessage());
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateData(@RequestBody ValidationRequest request) {
        try {
            ValidationResponse response = externalApiService.validateData(request.getData());
            return ResponseEntity.ok(response);
        } catch (ExternalApiException e) {
            if (log.isErrorEnabled()) {
                log.error("Failed to validate data: {}", e.getMessage());
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    private String sanitizeForLog(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[\\r\\n\\t\\f\\u0000-\\u001F\\u007F]", "_");
    }
}
