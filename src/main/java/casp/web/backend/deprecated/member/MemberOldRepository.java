package casp.web.backend.deprecated.member;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface MemberOldRepository extends MongoRepository<Member, UUID> {
}
