package com.fiap.nova.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

public class SwaggerConfig {

    OpenAPI config() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nova - API")
                        .version("1.0.0")
                        .description("RESTfull API for NOVA's system - Professional goal management platform with personalized recommendations using AI.")
                        .contact(new Contact()
                                .name("Team NOVA")
                                .url("https://github.com/joaoGFG/NovaJava")));
    }
}