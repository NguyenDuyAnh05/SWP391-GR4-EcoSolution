package org.swp391_group4_backend.ecosolution.config;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/**") // Áp dụng cho tất cả API bắt đầu bằng /api/v1/
                .allowedOrigins("http://localhost:5173", "http://localhost:5174") // CHÍNH XÁC là cổng của Vite/React
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
