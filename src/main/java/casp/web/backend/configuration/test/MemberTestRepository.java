package casp.web.backend.configuration.test;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberTestRepository extends MongoRepository<MemberProperty, UUID> {
    Optional<MemberProperty> findOneByEmail(String email);
}
