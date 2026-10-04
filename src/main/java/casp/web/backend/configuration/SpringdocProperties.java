package casp.web.backend.configuration;

import io.swagger.v3.oas.models.servers.Server;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

//This class is used for configuration,
// and the setter methods populate its properties when the Springdoc-related configuration values are provided.
@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties(prefix = "springdoc")
class SpringdocProperties {
    @Getter
    private final Set<Server> servers = new HashSet<>();
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String version;

    void setServers(Set<Server> servers) {
        // Clear existing servers to remove unnecessary entries
        this.servers.clear();
        servers.forEach(server -> server.setUrl(server.getUrl() + "/admin-v2"));
        this.servers.addAll(servers);
    }
}
