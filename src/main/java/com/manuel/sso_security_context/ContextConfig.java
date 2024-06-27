package com.manuel.sso_security_context;

/*
* This record is responsible for creating an instance of the arguments needed by the {@link CustomHandlerInterceptor}
*
*/
public record ContextConfig(String repoName, String methodName, String prefix, String accessLevelFieldName, String permissionsFieldName) {
}