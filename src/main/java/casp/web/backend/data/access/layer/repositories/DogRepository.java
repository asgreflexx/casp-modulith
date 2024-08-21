package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * IDogRepository
 *
 * @author sarah
 */

public interface DogRepository extends MongoRepository<Dog, UUID>, QuerydslPredicateExecutor<Dog> {

    Set<Dog> findAllByChipNumber(String chipNumber);

    Set<Dog> findAllByOwnerNameAndName(String ownerName, String name);

    Optional<Dog> findDogByEntityStatusAndId(EntityStatus entityStatus, UUID id);

    Set<Dog> findAllByEntityStatus(EntityStatus entityStatus);

    Set<Dog> findAllByEntityStatusAndName(EntityStatus entityStatus, String name);

}
