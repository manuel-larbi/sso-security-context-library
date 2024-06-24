package com.manuel.sso_security_context;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class RegisterHandler implements WebMvcConfigurer {
    private final CustomHandlerInterceptor customHandlerInterceptor;

    public RegisterHandler(CustomHandlerInterceptor customHandlerInterceptor) {
        this.customHandlerInterceptor = customHandlerInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(customHandlerInterceptor).addPathPatterns("/**");
    }
}