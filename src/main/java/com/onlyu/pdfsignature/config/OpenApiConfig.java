package com.onlyu.pdfsignature.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pdfSignatureOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PDF Signature Backend API")
                        .description("API de signature visuelle de documents PDF")
                        .version("v1")
                        .contact(new Contact().name("OnlyU"))
                        .license(new License().name("Propriétaire")));
    }
}
