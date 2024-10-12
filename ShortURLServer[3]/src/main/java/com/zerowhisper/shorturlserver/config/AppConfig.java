package com.zerowhisper.shorturlserver.config;

import com.zerowhisper.shorturlserver.service.SnowflakeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    @Bean
    public SnowflakeGenerator snowflakeGenerator() {
        return new SnowflakeGenerator(1);
    }
}
