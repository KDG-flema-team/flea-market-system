package com.example.fleamarketsystem.config;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

        @Value("${cors.allowed-origins:}")
        private String allowedOrigins;

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();

                // デフォルトのローカル開発用オリジン
                List<String> origins = new ArrayList<>(Arrays.asList(
                                "http://localhost",        // Docker Nginx (port 80)
                                "http://localhost:80",     // Docker Nginx (explicit port)
                                "http://localhost:3000",   // React dev server
                                "http://localhost:5173",   // Vite dev server
                                "http://localhost:8080"    // Backend (for testing)
                ));

                // 環境変数から追加のオリジンを読み込む
                if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
                        String[] additionalOrigins = allowedOrigins.split(",");
                        for (String origin : additionalOrigins) {
                                String trimmed = origin.trim();
                                if (!trimmed.isEmpty() && !origins.contains(trimmed)) {
                                        origins.add(trimmed);
                                }
                        }
                }

                configuration.setAllowedOrigins(origins);
                configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

                configuration.setAllowedHeaders(Arrays.asList("*"));
                configuration.setAllowCredentials(true);
                configuration.setMaxAge(3600L);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}
