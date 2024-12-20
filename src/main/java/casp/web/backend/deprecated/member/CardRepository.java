package casp.web.backend.deprecated.member;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface CardRepository extends MongoRepository<Card, UUID> {
    Set<Card> findAllByMemberId(UUID memberId);
}
