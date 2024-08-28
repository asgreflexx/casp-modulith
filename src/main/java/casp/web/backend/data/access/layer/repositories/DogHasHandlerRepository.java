package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.dog.Dog;
import casp.web.backend.data.access.layer.documents.dog.DogHasHandler;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.member.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * IDogHasHandlerRepository
 *
 * @author sarah
 */

public interface DogHasHandlerRepository extends MongoRepository<DogHasHandler, UUID>, QuerydslPredicateExecutor<DogHasHandler>, DogHasHandlerCustomRepository {

    /**
     * @deprecated use {@link #findAllByMember(Member)} instead
     */
    @Deprecated(forRemoval = true)
    List<DogHasHandler> findAllByMemberId(UUID memberId);

    Set<DogHasHandler> findAllByMember(Member member);

    /**
     * @deprecated use {@link #findAllByDog(Dog)} instead
     */
    @Deprecated(forRemoval = true)
    List<DogHasHandler> findAllByDogId(UUID dogId);

    Set<DogHasHandler> findAllByDog(Dog dog);

    /**
     * @deprecated use {@link #findAllByDogAndEntityStatus(Dog, EntityStatus)} instead
     */
    @Deprecated(forRemoval = true)
    Set<DogHasHandler> findAllByDogIdAndEntityStatus(UUID dogId, EntityStatus entityStatus);

    Set<DogHasHandler> findAllByDogIdAndEntityStatusNot(UUID dogId, EntityStatus entityStatus);

    Set<DogHasHandler> findAllByDogAndEntityStatus(Dog dog, EntityStatus entityStatus);

    /**
     * @deprecated use {@link #findAllByMemberAndEntityStatus(Member, EntityStatus)} instead
     */
    @Deprecated(forRemoval = true)
    Set<DogHasHandler> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus);

    Set<DogHasHandler> findAllByMemberIdAndEntityStatusIsNot(UUID memberId, EntityStatus entityStatus);

    Set<DogHasHandler> findAllByMemberAndEntityStatus(Member member, EntityStatus entityStatus);

    /**
     * @deprecated use {@link findAllByEntityStatusAndDogInOrMemberIn(EntityStatus, Set, Set)} instead
     */
    @Deprecated(forRemoval = true)
    List<DogHasHandler> findAllByEntityStatusAndDogIdInOrMemberIdIn(EntityStatus entityStatus, List<UUID> dogIds, List<UUID> memberIds);

    Set<DogHasHandler> findAllByEntityStatus(EntityStatus entityStatus);

    Set<DogHasHandler> findAllByEntityStatusAndIdIn(EntityStatus entityStatus, Set<UUID> ids);

    /**
     * I will search for any {@link DogHasHandler} by the memberId and dogId and not this entityStatus
     * @deprecated use {@link #findOneByMemberAndDogAndEntityStatusNotLike(Member, Dog, EntityStatus)} instead
     * @param memberId     the {@link Member#id}
     * @param dogId        the {@link Dog#id}
     * @param entityStatus the {@link EntityStatus}
     * @return one instance of {@link DogHasHandler} or empty
     */
    @Deprecated(forRemoval = true)
    Optional<DogHasHandler> findOneByMemberIdAndDogIdAndEntityStatusNotLike(UUID memberId, UUID dogId, EntityStatus entityStatus);

    Optional<DogHasHandler> findOneByMemberAndDogAndEntityStatusNotLike(Member member, Dog dog, EntityStatus entityStatus);

    Optional<DogHasHandler> findDogHasHandlerByIdAndEntityStatus(UUID id, EntityStatus entityStatus);
}
