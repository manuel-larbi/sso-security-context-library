package com.manuel.sso_security_context;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * CustomContextHolderAutoConfig class is an autoconfiguration class
 * that provides beans for custom security context holder,
 * handler interceptor, and register handler.
 * It is conditionally autoconfigured when the {@link SecurityContextHolder} class is available.
 *
 * @author Manuel
 * @version 1.0
 * @see SecurityContextHolder
 */
@AutoConfiguration
@ConditionalOnClass(SecurityContextHolder.class)
public class CustomContextHolderAutoConfig {

    /**
     * ApplicationContext is the Spring application context that provides access to beans and other resources.
     */
    private final ApplicationContext applicationContext;

    /**
     * Constructor that initializes the CustomContextHolderAutoConfig with the provided ApplicationContext.
     *
     * @param applicationContext the Spring application context
     */
    public CustomContextHolderAutoConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * This method provides a bean for the custom security context holder.
     * It is conditionally created when a bean with the same name does not yet exist.
     *
     * @return a new instance of {@link CustomContextHolderImpl} with the provided ApplicationContext
     */
    @Bean
    @ConditionalOnMissingBean
    public CustomContextHolderImpl customSecurityContextHolder() {
        return new CustomContextHolderImpl(applicationContext);
    }

    /**
     * This method provides a bean for the custom handler interceptor.
     * It is conditionally created when a bean with the same name does not yet exist.
     *
     * @param contextConfig the context configuration
     * @return a new instance of {@link CustomHandlerInterceptor} with the provided ApplicationContext and context configuration
     */
    @Bean
    @ConditionalOnMissingBean
    public CustomHandlerInterceptor customHandlerInterceptor(ContextConfig contextConfig) {
        return new CustomHandlerInterceptor(applicationContext, contextConfig);
    }

    /**
     * This method provides a bean for the register handler.
     * It is conditionally created when a bean with the same name does not yet exist.
     *
     * @param customHandlerInterceptor the custom handler interceptor
     * @return a new instance of {@link RegisterHandler} with the provided custom handler interceptor
     */
    @Bean
    @ConditionalOnMissingBean
    public RegisterHandler registerHandler(CustomHandlerInterceptor customHandlerInterceptor) {
        return new RegisterHandler(customHandlerInterceptor);
    }
}