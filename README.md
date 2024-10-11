# Custom Security Context Library
This library is designed
to target specific patterns for implementing Spring Security configurations related to access levels in your project.
It helps developers streamline the security context setup
and minimize the boilerplate code required for configuring security,
particularly when working with JWT.

## Prerequisites
> To use this library effectively, the following components must be implemented to manage user roles and access control:

- A class to manage users in the system, handling user profiles and their associated roles.
- A class to manage access levels that defines various roles (e.g., 'Admin', 'Editor', 'Viewer') assigned to users, determining their level of access.
- A class to manage permissions that specifies actions (e.g., 'read', 'write', 'delete') and associates these actions with the corresponding access levels.

These components work together to establish a robust access control system, where each user is linked to an access level that determines their available permissions.

## Setup Guidelines
### Adding the dependency

- Include the following Maven dependency in your `pom.xml`:
```xml
<dependency>
  <groupId>io.github.manu-tech-code</groupId>
  <artifactId>sso-security-context</artifactId>
  <version>1.1</version>
</dependency>
```

### Bean Configuration

- To configure the library, you need to create a `@Bean` of type `ContextConfig`.

* Repository Name (`repositoryName`): The name of the repository to fetch the user records.
* Method Name (`methodName`): The method in the repository that retrieves the user by ID (should accept `user_id` of type `long`).
* Prefix (`prefix`): Prefix for roles (e.g., `ROLE_`, `SCOPE_`); if not needed, use an empty string.
* Access Level Field (`accessLevelFieldName`): The field name representing access levels in the parent class.
* Permissions Field (`permissionsFieldName`): The field name representing permissions in the AccessLevel class.

> Example of `@Bean` Declaration in Main class

```java
package org.somepackage;

import com.manuel.sso_security_context.context.ContextConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ContextConfig contextConfig() {
        return new ContextConfig(
                "employeeRepository",
                "findByUserId",
                "ROLE_",
                "accessLevel",
                "permissions"
        );
    }
}
```

> **NB:** You can create a configuration class to initialize the `ContextConfig` bean

### OAuth2 Configuration

This library integrates with **Spring's OAuth2 Resource Server**.
You need to specify the location of your public key in the `application.yaml` or `application.properties` file.

#### Example configuration for `application.yaml`:
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          public-key-location: classpath:<your-public-key-file>
```
#### For `application.properties`:
```
spring.security.oauth2.resourceserver.jwt.public-key-location=classpath:<your-public-key-file>
```

- Ensure your SecurityFilterChain configuration includes:
```
.oauth2ResourceServer(oauth -> oauth
      .jwt(Customizer.withDefaults())
)
```

#### Example `SecurityFilterChain` Configuration
```java
package org.somepackage;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
@EnableMethodSecurity(securedEnabled = true)
public class TestSecurityConfig {
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, TestAuthenticationEntryPoint entryPoint) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth
                        .authenticationEntryPoint(entryPoint)
                        .jwt(Customizer.withDefaults())
                );

        return httpSecurity.build();
    }
}
```
### Verifying Security Context

To confirm that authorities are correctly set in the `SecurityContext`, you can log the `Authorities` from the `Authentication` object using:
```java
Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
if (authentication != null) {
    logger.info("Authorities: {}", authentication.getAuthorities());
}
```
