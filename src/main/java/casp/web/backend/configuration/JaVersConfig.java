package casp.web.backend.configuration;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JaVersConfig {

    @Bean
    AuthorProvider authorProvider() {
        return () -> "SYSTEM";
    }
}
