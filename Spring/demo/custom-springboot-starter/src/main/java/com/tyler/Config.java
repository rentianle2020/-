package com.tyler;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty("config")
public class Config {

    @Bean
    public MyBean myBean() {
        return new MyBean(24, "tyler");
    }
}
