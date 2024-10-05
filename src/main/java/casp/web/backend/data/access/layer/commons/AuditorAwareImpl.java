package casp.web.backend.data.access.layer.commons;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        /*
          if you are using spring security, you can get the currently logged username with following code segment.
          TODO SecurityContextHolder.getContext().getAuthentication().getName()
         */
        return Optional.of("Bonsai");
    }
}
