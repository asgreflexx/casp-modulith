package casp.web.backend.common.reference;

import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface DogReferenceRepository extends MongoRepository<DogReference, UUID> {
    Optional<DogReference> findOneByIdAndEntityStatus(UUID id, EntityStatus entityStatus);
}
