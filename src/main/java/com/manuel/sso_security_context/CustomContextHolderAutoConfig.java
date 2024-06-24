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
    private final ApplicationContext applicationContext;

    public CustomContextHolderAutoConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomContextHolderImpl customSecurityContextHolder(){
        return new CustomContextHolderImpl(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public CustomHandlerInterceptor customHandlerInterceptor(ContextConfig contextConfig){
        return new CustomHandlerInterceptor(applicationContext, contextConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    RegisterHandler registerHandler(CustomHandlerInterceptor customHandlerInterceptor){
        return new RegisterHandler(customHandlerInterceptor);
    }
}