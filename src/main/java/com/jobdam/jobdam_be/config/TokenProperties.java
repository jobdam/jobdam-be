package com.jobdam.jobdam_be.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class TokenProperties {
    private TokenConfig accessToken;
    private TokenConfig refreshToken;

    @Setter
    @Getter
    public static class TokenConfig {
        private String name;
        private long expiry;

    }
}
