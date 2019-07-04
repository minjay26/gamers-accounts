package org.minjay.gamers.accounts.resource.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ComponentScan(basePackages = "org.minjay.gamers.accounts.rest.controller")
public class RestMvcConfig implements WebMvcConfigurer {
}
