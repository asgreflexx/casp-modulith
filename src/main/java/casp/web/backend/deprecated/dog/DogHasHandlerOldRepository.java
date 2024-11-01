package casp.web.backend.deprecated.dog;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface DogHasHandlerOldRepository extends MongoRepository<DogHasHandler, UUID> {
}
