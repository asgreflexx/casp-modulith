package casp.web.backend.common.reference;

import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface MemberReferenceRepository extends MongoRepository<MemberReference, UUID> {
    Optional<MemberReference> findOneByIdAndEntityStatus(UUID id, EntityStatus entityStatus);
}
