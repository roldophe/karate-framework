package com.example.karateframework.service;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.example.karateframework.dto.ExternalUserResponse;
import com.example.karateframework.dto.ValidationRequest;
import com.example.karateframework.dto.ValidationResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiService {

    private final RestClient restClient;

    public ExternalUserResponse getExternalUser(String userId) {
        String safeUserId = sanitizeForLog(userId);
        log.debug("Fetching external user with ID: {}", safeUserId);

        return restClient
                .get()
                .uri("/api/external/users/{id}", userId)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            log.error(
                                    "Client error while fetching user {}: {}",
                                    safeUserId,
                                    response.getStatusCode());
                            throw new RuntimeException(
                                    "External API returned client error: "
                                            + response.getStatusCode());
                        })
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            log.error(
                                    "Server error while fetching user {}: {}",
                                    safeUserId,
                                    response.getStatusCode());
                            throw new RuntimeException(
                                    "External API returned server error: "
                                            + response.getStatusCode());
                        })
                .body(ExternalUserResponse.class);
    }

    public ValidationResponse validateData(String data) {
        String safeData = sanitizeForLog(data);
        log.debug("Validating data: {}", safeData);

        ValidationRequest request = new ValidationRequest(data);

        return restClient
                .post()
                .uri("/api/external/validate")
                .body(request)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        (req, response) -> {
                            log.error("Error while validating data: {}", response.getStatusCode());
                            throw new RuntimeException(
                                    "External API validation error: " + response.getStatusCode());
                        })
                .body(ValidationResponse.class);
    }

    private String sanitizeForLog(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[\\r\\n\\t\\f\\u0000-\\u001F\\u007F]", "_");
    }
}
