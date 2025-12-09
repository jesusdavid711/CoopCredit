package com.coopcredit.app.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CoopCredit - Credit Application Service API")
                        .version("1.0.0")
                        .description("API REST para gestión de solicitudes de crédito con arquitectura hexagonal")
                        .contact(new Contact()
                                .name("CoopCredit Team")
                                .email("support@coopcredit.com")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Ingresa el token JWT recibido al hacer login")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
