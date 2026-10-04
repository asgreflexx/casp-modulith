package casp.web.backend.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {
    private final SpringdocProperties springdocProperties;

    @Autowired
    OpenApiConfig(SpringdocProperties springdocProperties) {
        this.springdocProperties = springdocProperties;
    }

    @Bean
    OpenAPI openAPI() {
        var bearerKey = "bearer-key";
        var openAPI = new OpenAPI()
                .info(new Info()
                        .title(springdocProperties.getTitle())
                        .version(springdocProperties.getVersion()))
                .addSecurityItem(new SecurityRequirement().addList(bearerKey))
                .components(new Components()
                        .addSecuritySchemes(bearerKey,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
        springdocProperties.getServers().forEach(openAPI::addServersItem);
        return openAPI;
    }
}
