package com.manuel.sso_security_context;

import org.springframework.security.core.context.SecurityContext;

import java.lang.reflect.InvocationTargetException;


public interface CustomContextHolder {
    /**
     * @param permissionPrefix The permission prefix to use if needed. Default is null. Example: "SCOPE_"
     * @param repoName This the name of the repository to use for retrieving the access level
     * @param methodName The name of the method to call on {@param repoName} to retrieve the access level
     * @return {@link SecurityContext}
     * @throws NoSuchMethodException if the {@param methodName} is not found on the {@param repoName}
     * @throws InvocationTargetException if the target instance of the {@param repoName} bean is not defined.
     * @throws IllegalAccessException if the fields in the object returned are not accessible.
     */
    SecurityContext getContext(String permissionPrefix, String repoName, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException;
}