package com.manuel.sso_security_context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.manuel.sso_security_context.CustomContextHolderImpl.setAccessLevelFieldName;
import static com.manuel.sso_security_context.CustomContextHolderImpl.setPermissionsFieldName;

/**
 * This class is a HandlerInterceptor that intercepts the execution of handler methods.
 * If it is, it retrieves the custom security context from the {@link CustomContextHolder}
 * and sets it in the {@link SecurityContextHolder}.
 *
 * @author Manuel
 * @version 1.0
 * @see CustomContextHolder
 * @see SecurityContextHolder
 */
@Component
public class CustomHandlerInterceptor implements HandlerInterceptor {
    private final ApplicationContext applicationContext;
    private final ContextConfig contextConfig;

    /**
     * Instantiates a new Custom handler interceptor.
     *
     * @param applicationContext the application context
     * @param contextConfig      the context config
     */
    public CustomHandlerInterceptor(ApplicationContext applicationContext, ContextConfig contextConfig) {
        this.applicationContext = applicationContext;
        this.contextConfig = contextConfig;
    }

    /**
     * This class is a HandlerInterceptor that intercepts the execution of handler methods.
     * If it is, it retrieves the custom security context from the {@link CustomContextHolder}
     * and sets it in the {@link SecurityContextHolder}.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param handler  the handler method
     * @return true if the handler method should be executed, false otherwise
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull Object handler) {
        CustomContextHolderImpl c = new CustomContextHolderImpl(applicationContext);
        c.setRepoName(contextConfig.repoName());
        c.setPrefix(contextConfig.prefix());
        c.setMethodName(contextConfig.methodName());
        setAccessLevelFieldName(contextConfig.accessLevelFieldName());
        setPermissionsFieldName(contextConfig.permissionsFieldName());

        SecurityContextHolder.setContext(c.getContext());

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