package com.learnlab.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "learnlab")
public record AppProperties(Jwt jwt, String uploadDir, Seed seed) {

  public record Jwt(String secret, long expirationMinutes) {
  }

  public record Seed(boolean enabled) {
  }
}
