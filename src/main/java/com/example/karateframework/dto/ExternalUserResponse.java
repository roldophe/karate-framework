package com.example.karateframework.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalUserResponse {
    private String id;
    private String name;
    private String email;
    private String source;
}
