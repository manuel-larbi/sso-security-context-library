package com.manuel.sso_security_context;

import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

@Component
public class CustomGraphQlInterceptor implements WebGraphQlInterceptor {
    private final CustomContextHolder customContextHolder;

    public CustomGraphQlInterceptor(CustomContextHolder customContextHolder) {
        this.customContextHolder = customContextHolder;
    }

    @Override
    @NonNull
    public Mono<WebGraphQlResponse> intercept(@NonNull WebGraphQlRequest request,@NonNull Chain chain) {
        setCustomSecurityContext(request);
        return chain.next(request);
    }

    @Override
    @NonNull
    public WebGraphQlInterceptor andThen(@NonNull WebGraphQlInterceptor nextInterceptor) {
        return WebGraphQlInterceptor.super.andThen(nextInterceptor);
    }

    @Override
    @NonNull
    public Chain apply(@NonNull Chain chain) {
        return WebGraphQlInterceptor.super.apply(chain);
    }

    private void setCustomSecurityContext(WebGraphQlRequest request) {
        CustomSecurityContext classAnnotation = request.getClass().getAnnotation(CustomSecurityContext.class);

        if (classAnnotation != null) {
            String prefix = classAnnotation.prefix();
            String repoName = classAnnotation.repoName();
            String methodName = classAnnotation.methodName();

            SecurityContext customContext;
            try {
                customContext = customContextHolder.getContext(prefix, repoName, methodName);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            SecurityContextHolder.setContext(customContext);
        }
    }
}
