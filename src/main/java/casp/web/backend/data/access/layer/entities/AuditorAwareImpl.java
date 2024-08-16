package casp.web.backend.data.access.layer.entities;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<String> {

    // FIXME This should be added automatically
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("Bonsai");
    }
}
