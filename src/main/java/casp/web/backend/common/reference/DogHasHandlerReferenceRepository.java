package casp.web.backend.common.reference;

import casp.web.backend.common.enums.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface DogHasHandlerReferenceRepository extends MongoRepository<DogHasHandlerReference, UUID>, DogHasHandlerReferenceCustomRepository {
    Optional<DogHasHandlerReference> findOneByIdAndEntityStatus(UUID id, EntityStatus entityStatus);
}
