package com.manuel.sso_security_context;

import com.manuel.sso_security_context.exception.CustomContextException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * CustomContextHolderImpl class provides a way to retrieve the {@link SecurityContext}
 * based on the user's token and the specified repository and method.
 *
 * @author Manuel
 * @version 1.0
 * @see SecurityContext
 */
@Component
public class CustomContextHolderImpl implements CustomContextHolder {

    /**
     * The Spring {@link ApplicationContext} to retrieve beans.
     */
    private final ApplicationContext applicationContext;

    /**
     * The prefix to be added to the permissions.
     */
    private String prefix;

    /**
     * The name of the repository bean to be used.
     */
    private String repoName;

    /**
     * The name of the method to be invoked on the repository bean.
     */
    private String methodName;

    /**
     * The name of the field containing the access level in the returned object.
     */
    private static String accessLevelFieldName;

    /**
     * The name of the field containing the permissions in the returned object.
     */
    private static String permissionsFieldName;

    /**
     * Constructor that initializes the {@link ApplicationContext}.
     *
     * @param applicationContext the Spring {@link ApplicationContext}
     */
    public CustomContextHolderImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Sets the prefix to be added to the permissions.
     *
     * @param prefix the prefix to be added
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Sets the name of the repository bean to be used.
     *
     * @param repoName the name of the repository bean
     */
    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    /**
     * Sets the name of the method to be invoked on the repository bean.
     *
     * @param methodName the name of the method
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Sets the name of the field containing the access level in the returned object.
     *
     * @param accessLevelFieldName the name of the field
     */
    public static void setAccessLevelFieldName(String accessLevelFieldName) {
        CustomContextHolderImpl.accessLevelFieldName = accessLevelFieldName;
    }

    /**
     * Sets the name of the field containing the permissions in the returned object.
     *
     * @param permissionsFieldName the name of the field
     */
    public static void setPermissionsFieldName(String permissionsFieldName) {
        CustomContextHolderImpl.permissionsFieldName = permissionsFieldName;
    }

    /**
     * Retrieves the {@link SecurityContext} based on the user's token and the specified repository and method.
     *
     * @return the {@link SecurityContext}
     */
    @Override
    public SecurityContext getContext() {
        Jwt token = (Jwt) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Object repoBean = applicationContext.getBean(repoName);
        Class<?> repoClass = repoBean.getClass();

        long userId = token.getClaim("user_id");

        List<GrantedAuthority> authorities = new ArrayList<>();

        Arrays.stream(repoClass.getDeclaredMethods()).filter(match -> match.getName().equals(methodName)).forEach(
                method -> {
                    try {
                        Object result = method.invoke(repoBean, userId);
                        if (result instanceof Optional && ((Optional<?>) result).isPresent()){
                            Object permissionsObject = getObject((Optional<?>) result);

                            if (permissionsObject instanceof List<?> permissions){
                                for (Object permission : permissions) {
                                    Field name = permission.getClass().getDeclaredField("name");
                                    name.setAccessible(true);

                                    String p = name.get(permission).toString();

                                    authorities.add(new SimpleGrantedAuthority(
                                            prefix.isEmpty() ? p : prefix+p
                                    ));
                                }
                            }
                        }

                    } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
                        throw new CustomContextException(e);
                    }
                }
        );

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(token, null, authorities));

        return SecurityContextHolder.getContext();
    }

    /**
     * Retrieves the object containing the permissions from the given optional result.
     *
     * @param result the optional result
     * @return the object containing the permissions
     * @throws NoSuchFieldException if the field is not found
     * @throws IllegalAccessException if the field cannot be accessed
     */
    private static Object getObject(Optional<?> result) throws NoSuchFieldException, IllegalAccessException {
        Object o = result.orElse(null);

        assert o != null;
        Field a = o.getClass().getDeclaredField(accessLevelFieldName);
        a.setAccessible(true);
        Object accessLevelObject = a.get(o);

        Field p = accessLevelObject.getClass().getDeclaredField(permissionsFieldName);
        p.setAccessible(true);
        return p.get(accessLevelObject);
    }
}