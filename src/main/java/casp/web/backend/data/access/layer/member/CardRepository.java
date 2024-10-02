package casp.web.backend.data.access.layer.member;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CardRepository extends MongoRepository<Card, UUID>, QuerydslPredicateExecutor<Card> {
    Set<Card> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus);

    Optional<Card> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

    Optional<Card> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Set<Card> findAllByMemberIdAndEntityStatusNot(UUID memberId, EntityStatus entityStatus);
}
