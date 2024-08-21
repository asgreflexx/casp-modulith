package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.member.Card;
import casp.web.backend.data.access.layer.documents.member.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Set;
import java.util.UUID;

public interface CardRepository extends MongoRepository<Card, UUID>, QuerydslPredicateExecutor<Card> {

    /**
     * @deprecated use {@link #findAllByMemberAndEntityStatus(Member, EntityStatus)} instead
     */
    @Deprecated(forRemoval = true)
    Set<Card> findAllByMemberIdAndEntityStatus(UUID memberId, EntityStatus entityStatus);

    Set<Card> findAllByMemberAndEntityStatus(Member member, EntityStatus entityStatus);
}
