package com.manuel.sso_security_context;

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

@Component
public class CustomContextHolderImpl implements CustomContextHolder {
    private final ApplicationContext applicationContext;
    private String prefix;
    private String repoName;
    private String methodName;
    private static String accessLevelFieldName;
    private static String permissionsFieldName;

    public CustomContextHolderImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public static void setAccessLevelFieldName(String accessLevelFieldName) {
        CustomContextHolderImpl.accessLevelFieldName = accessLevelFieldName;
    }

    public static void setPermissionsFieldName(String permissionsFieldName) {
        CustomContextHolderImpl.permissionsFieldName = permissionsFieldName;
    }

    /**
     * @return {@link SecurityContext}
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
                        throw new RuntimeException(e);
                    }
                }
        );

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(token, null, authorities));

        return SecurityContextHolder.getContext();
    }

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