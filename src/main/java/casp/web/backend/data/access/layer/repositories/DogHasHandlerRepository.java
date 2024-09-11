package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.custom.repositories.DogHasHandlerCustomRepository;
import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * IDogHasHandlerRepository
 *
 * @author sarah
 */

public interface DogHasHandlerRepository extends MongoRepository<DogHasHandler, UUID>, QuerydslPredicateExecutor<DogHasHandler>, DogHasHandlerCustomRepository {

    Set<DogHasHandler> findAllByDogIdAndEntityStatus(UUID dogId, EntityStatus entityStatus);

    Set<DogHasHandler> findAllByDogIdAndEntityStatusNot(UUID dogId, EntityStatus entityStatus);

    Set<DogHasHandler> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus);

    Set<DogHasHandler> findAllByMemberIdAndEntityStatusIsNot(UUID memberId, EntityStatus entityStatus);

    Set<DogHasHandler> findAllByEntityStatus(EntityStatus entityStatus);

    Set<DogHasHandler> findAllByEntityStatusAndIdIn(EntityStatus entityStatus, Set<UUID> ids);

    Optional<DogHasHandler> findDogHasHandlerByIdAndEntityStatus(UUID id, EntityStatus entityStatus);
}
