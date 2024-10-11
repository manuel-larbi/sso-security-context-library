package com.manuel.sso_security_context.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * RegisterHandler class is a configuration class that registers a custom interceptor for all paths.
 * It implements the WebMvcConfigurer interface to add the custom interceptor to the application's interceptor registry.
 *
 * @author Manuel
 * @version 1.0
 */
@Component
public class RegisterHandler implements WebMvcConfigurer {

    /**
     * CustomHandlerInterceptor instance to be registered as an interceptor.
     */
    private final CustomHandlerInterceptor customHandlerInterceptor;

    /**
     * Constructor that initializes the RegisterHandler with the provided CustomHandlerInterceptor instance.
     *
     * @param customHandlerInterceptor the CustomHandlerInterceptor instance to be registered
     */
    public RegisterHandler(CustomHandlerInterceptor customHandlerInterceptor) {
        this.customHandlerInterceptor = customHandlerInterceptor;
    }

    /**
     * Adds the custom interceptor to the application's interceptor registry for all paths.
     *
     * @param registry the InterceptorRegistry instance to add the interceptor to
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customHandlerInterceptor).addPathPatterns("/**");
    }
}