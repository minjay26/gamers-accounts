package org.minjay.gamers.accounts.resource.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

@Configuration
public class OAuth2ServerConfig {

    @Configuration
    @EnableAuthorizationServer
    static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
        @Autowired
        private DataSource dataSource;

        @Autowired
        private AuthenticationManager authenticationManager;
        @Autowired
        private RedisConnectionFactory connectionFactory;

        @Bean
        public TokenStore tokenStore() {
            return new RedisTokenStore(connectionFactory);
        }

        @Bean
        public ClientDetailsService clientDetails() {
            return new JdbcClientDetailsService(dataSource);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.withClientDetails(clientDetails());
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.authenticationManager(authenticationManager);
            endpoints.tokenStore(tokenStore());

            DefaultTokenServices tokenServices = new DefaultTokenServices();
            tokenServices.setTokenStore(tokenStore());
            tokenServices.setSupportRefreshToken(true);
            tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
            tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
            tokenServices.setAccessTokenValiditySeconds(60 * 30); // 30天
            endpoints.tokenServices(tokenServices);
        }
    }
}
