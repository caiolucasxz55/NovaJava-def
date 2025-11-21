package com.fiap.nova.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DotEnvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        
        try {
            var envPath = Paths.get(".env");
            if (Files.exists(envPath)) {
                Map<String, Object> props = new HashMap<>();
                
                Files.lines(envPath)
                    .filter(line -> !line.trim().isEmpty())
                    .filter(line -> !line.trim().startsWith("#"))
                    .filter(line -> line.contains("="))
                    .forEach(line -> {
                        String[] parts = line.split("=", 2);
                        props.put(parts[0].trim(), parts[1].trim());
                    });
                
                environment.getPropertySources().addFirst(new MapPropertySource("dotenv", props));
                System.out.println("Loaded " + props.size() + " variables from .env file");
            }
        } catch (IOException e) {
            System.err.println("rror loading .env: " + e.getMessage());
        }
    }
}
