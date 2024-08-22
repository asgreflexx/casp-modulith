package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * IDogRepository
 *
 * @author sarah
 */

public interface DogRepository extends MongoRepository<Dog, UUID>, QuerydslPredicateExecutor<Dog> {

    Optional<Dog> findDogByChipNumberAndEntityStatus(String chipNumber, EntityStatus entityStatus);

    List<Dog> findAllByOwnerNameAndNameAndEntityStatusOrderByNameAscOwnerNameAsc(String ownerName, String name, EntityStatus entityStatus);

    Optional<Dog> findDogByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Optional<Dog> findDogByIdAndEntityStatusIsNot(UUID id, EntityStatus entityStatus);

    Page<Dog> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);

    Set<Dog> findAllByEntityStatusAndName(EntityStatus entityStatus, String name);

}
