package com.devvault.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:devvault}")
    private String appName;

    @Value("${application.version:${project.version:0.0.1-SNAPSHOT}}")
    private String appVersion;

    @Bean
    public OpenAPI devVaultOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(appName + " API")
                        .description("REST API for DevVault backend services")
                        .version(appVersion)
                        .license(new License().name("Proprietary")))
                .externalDocs(new ExternalDocumentation()
                        .description("DevVault docs")
                        .url("https://example.com/docs"));
    }
}
