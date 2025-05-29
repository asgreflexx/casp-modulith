package casp.web.backend.member.data;

import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@JaversSpringDataAuditable
public interface MemberRepository extends BaseRepository<Member>, MemberCustomRepository {

    Optional<Member> findOneByEmail(String email);

    Set<Member> findAllByIdInAndEntityStatus(Set<UUID> membersId, EntityStatus entityStatus);
}
