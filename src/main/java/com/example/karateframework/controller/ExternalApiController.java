package com.example.karateframework.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.karateframework.dto.ExternalUserResponse;
import com.example.karateframework.dto.ValidationRequest;
import com.example.karateframework.dto.ValidationResponse;
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
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponse> validateData(@RequestBody ValidationRequest request) {
        try {
            ValidationResponse response = externalApiService.validateData(request.getData());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
