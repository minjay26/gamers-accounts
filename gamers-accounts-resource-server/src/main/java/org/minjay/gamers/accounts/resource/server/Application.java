package org.minjay.gamers.accounts.resource.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.minjay.gamers.accounts.upload.qcloud.QCloudConfig;
import org.minjay.gamers.security.jackson.SecurityLoginJackson2Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import(QCloudConfig.class)
@EnableAsync
@EnableConfigurationProperties
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @EnableTransactionManagement
    @EnableJpaRepositories(basePackages = "org.minjay.gamers.accounts.data.repository")
    @EnableJpaAuditing
    @EntityScan(basePackages = "org.minjay.gamers.accounts.data.domain")
    static class JpaConfig {
    }

    @Configuration
    @ComponentScan(basePackages = "org.minjay.gamers.accounts.service")
    static class ServiceConfig {
    }

    @Bean
    public ValueOperations<String, String> valueOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SecurityLoginJackson2Module());
        return objectMapper;
    }

}
