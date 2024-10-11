package com.manuel.sso_security_context.context;

import org.springframework.security.core.context.SecurityContext;


/**
 * The interface Custom context holder.
 */
public interface CustomContextHolder {
    /**
     * Gets the security context.
     *
     * @return the context
     */
    SecurityContext getContext();
}