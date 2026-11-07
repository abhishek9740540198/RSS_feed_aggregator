package com.rssserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Mirrors the cors.Handler(cors.Options{...}) block in main.go's setupRouter:
 *   AllowedOrigins: []string{"https://*", "http://*"}
 *   AllowedMethods: GET, POST, PUT, DELETE, OPTIONS
 *   AllowedHeaders: "*"
 *   ExposedHeaders: "Link"
 *   AllowCredentials: false
 *   MaxAge: 300
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("https://*", "http://*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Link")
                .allowCredentials(false)
                .maxAge(300);
    }
}
