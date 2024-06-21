package com.manuel.sso_security_context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomSecurityContext {
    String prefix() default "";
    String repoName() default "";
    String methodName() default "";
}