package com.manuel.sso_security_context;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

@AutoConfiguration
@ConditionalOnClass(SecurityContextHolder.class)
public class CustomContextHolderAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public CustomSecurityContextHolderImpl customSecurityContextHolder(ApplicationContext applicationContext){
        return new CustomSecurityContextHolderImpl(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomHandlerInterceptor customHandlerInterceptor(CustomContextHolder customContextHolder){
        return new CustomHandlerInterceptor(customContextHolder);
    }

    @Bean
    @ConditionalOnMissingBean
    RegisterHandler registerHandler(CustomHandlerInterceptor customHandlerInterceptor){
        return new RegisterHandler(customHandlerInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomGraphQlInterceptor customGraphQlInterceptor(CustomContextHolder customContextHolder){
        return new CustomGraphQlInterceptor(customContextHolder);
    }
}