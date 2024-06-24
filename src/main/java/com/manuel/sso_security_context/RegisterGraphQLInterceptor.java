package com.manuel.sso_security_context;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.stereotype.Component;

@Component
public class RegisterGraphQLInterceptor {
    @Bean
    public WebGraphQlHandler webGraphQlHandler(ExecutionGraphQlService service, ObjectProvider<WebGraphQlInterceptor> interceptors) {
        return WebGraphQlHandler.builder(service)
                .interceptors(interceptors.orderedStream().toList())
                .build();
    }

    @Bean
    public GraphQlHttpHandler graphQlHttpHandler(WebGraphQlHandler webGraphQlHandler) {
        return new GraphQlHttpHandler(webGraphQlHandler);
    }
}
