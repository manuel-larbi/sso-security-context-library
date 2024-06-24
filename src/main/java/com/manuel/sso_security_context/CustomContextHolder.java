package com.manuel.sso_security_context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.transaction.annotation.Transactional;


public interface CustomContextHolder {
    @Transactional
    SecurityContext getContext();
}