package com.manuel.sso_security_context;

public record ContextConfig(String repoName, String methodName, String prefix, String accessLevelFieldName, String permissionsFieldName) {
}