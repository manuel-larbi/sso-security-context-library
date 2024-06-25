package com.manuel.sso_security_context;

import org.springframework.security.core.context.SecurityContext;


public interface CustomContextHolder {
    SecurityContext getContext();
}