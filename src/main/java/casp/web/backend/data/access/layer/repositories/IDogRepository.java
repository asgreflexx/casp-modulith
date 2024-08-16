package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.entities.Dog;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * IDogRepository
 *
 * @author sarah
 */

public interface IDogRepository extends MongoRepository<Dog, UUID> {

    List<Dog> findAllByChipNumber(String chipNumber);

    List<Dog> findAllByOwnerNameAndName(String ownerName, String name);

    Optional<Dog> findDogByEntityStatusAndId(EntityStatus entityStatus, UUID id);

    List<Dog> findAllByEntityStatus(EntityStatus entityStatus);

    List<Dog> findAllByEntityStatusAndName(EntityStatus entityStatus, String name);

}
