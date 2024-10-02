package casp.web.backend.data.access.layer.dog;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.UUID;

/**
 * IDogRepository
 *
 * @author sarah
 */

public interface DogRepository extends MongoRepository<Dog, UUID>, QuerydslPredicateExecutor<Dog>, DogCustomRepository {

    Optional<Dog> findDogByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Page<Dog> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);
}
