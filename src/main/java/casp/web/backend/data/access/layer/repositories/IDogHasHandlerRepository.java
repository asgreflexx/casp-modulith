package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.entities.DogHasHandler;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * IDogHasHandlerRepository
 *
 * @author sarah
 */

public interface IDogHasHandlerRepository extends MongoRepository<DogHasHandler, UUID> {

    List<DogHasHandler> findAllByMemberId(UUID memberId);

    List<DogHasHandler> findAllByDogId(UUID dogId);

    List<DogHasHandler> findAllByEntityStatusAndDogId(EntityStatus entityStatus, UUID dogId);

    List<DogHasHandler> findAllByEntityStatusAndMemberId(EntityStatus entityStatus, UUID memberId);

    List<DogHasHandler> findAllByEntityStatusAndDogIdInOrMemberIdIn(EntityStatus entityStatus, List<UUID> dogIds, List<UUID> memberIds);

    List<DogHasHandler> findAllByEntityStatus(EntityStatus entityStatus);

    List<DogHasHandler> findAllByEntityStatusAndIdIn(EntityStatus entityStatus, List<UUID> ids);

    /**
     * I will search for any {@link DogHasHandler} by the memberId and dogId and not this entityStatus
     *
     * @param memberId     the {@link casp.web.backend.data.access.layer.entities.Member#id}
     * @param dogId        the {@link casp.web.backend.data.access.layer.entities.Dog#id}
     * @param entityStatus the {@link EntityStatus}
     * @return one instance of {@link DogHasHandler} or empty
     */
    Optional<DogHasHandler> findOneByMemberIdAndDogIdAndEntityStatusNotLike(UUID memberId, UUID dogId, EntityStatus entityStatus);
}
