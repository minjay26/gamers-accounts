package org.minjay.gamers.accounts.resource.server.config;

import org.minjay.gamers.accounts.mail.MailSendSupport;
import org.minjay.gamers.accounts.mail.MailSender;
import org.minjay.gamers.accounts.rest.bind.support.CurrentUserHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ComponentScan(basePackages = "org.minjay.gamers.accounts.rest.controller")
public class RestMvcConfig implements WebMvcConfigurer {

    @Bean
    public CurrentUserHandlerMethodArgumentResolver currentUserHandlerMethodArgumentResolver() {
        return new CurrentUserHandlerMethodArgumentResolver();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserHandlerMethodArgumentResolver());
    }

    @Bean
    public MailSender customMailSender() {
        return new MailSendSupport();
    }

}
