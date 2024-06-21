package com.manuel.sso_security_context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class CustomHandlerInterceptor implements HandlerInterceptor {
    private final CustomContextHolder customContextHolder;

    public CustomHandlerInterceptor(CustomContextHolder customContextHolder) {
        this.customContextHolder = customContextHolder;
    }

    /**
     * This class is a HandlerInterceptor that intercepts the execution of handler methods.
     * It checks if the handler method is annotated with {@link CustomSecurityContext}.
     * If it is, it retrieves the custom security context from the {@link CustomContextHolder}
     * and sets it in the {@link SecurityContextHolder}.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param handler  the handler method
     * @return true if the handler method should be executed, false otherwise
     * @throws Exception if an error occurs during the interception process
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            Class<?> handlerClass = handlerMethod.getBeanType();
            CustomSecurityContext classAnnotation = handlerClass.getAnnotation(CustomSecurityContext.class);

            if (classAnnotation != null) {
                SecurityContext customContext = customContextHolder.getContext(classAnnotation.prefix(), classAnnotation.repoName(), classAnnotation.methodName());
                SecurityContextHolder.setContext(customContext);
            }
        }
        return true;
    }

    /**
     * This overridden methods is used to clear the SecurityContext from the SecurityContextHolder after the handler method has been executed.
     * This is done to prevent memory leaks.
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        SecurityContextHolder.clearContext();
    }
}