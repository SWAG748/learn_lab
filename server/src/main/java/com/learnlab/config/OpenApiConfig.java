package com.learnlab.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  OpenAPI learnlabOpenApi() {
    String scheme = "bearer";
    return new OpenAPI()
        .info(new Info()
            .title("LearnLab API")
            .version("v1")
            .description("学习工坊接口。先调用登录接口拿到 token，再点 Authorize，填入 Bearer token。"))
        .components(new Components().addSecuritySchemes(scheme, new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList(scheme));
  }
}
