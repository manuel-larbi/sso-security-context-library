package com.manuel.sso_security_context;

import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class CustomSecurityContextHolderImpl implements CustomContextHolder {
    private final ApplicationContext applicationContext;

    public CustomSecurityContextHolderImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * @return {@link SecurityContext}
     */
    @Transactional
    @Override
    public SecurityContext getContext(String prefix, String repoName, String methodName) {
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

                                    authorities.add(new SimpleGrantedAuthority(name.get(permission).toString()));
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
        Field accessLevelField = o.getClass().getDeclaredField("accessLevel");
        accessLevelField.setAccessible(true);
        Object accessLevelObject = accessLevelField.get(o);

        Field permissionsField = accessLevelObject.getClass().getDeclaredField("permissions");
        permissionsField.setAccessible(true);
        return permissionsField.get(accessLevelObject);
    }
}