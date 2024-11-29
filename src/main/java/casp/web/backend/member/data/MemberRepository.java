package casp.web.backend.member.data;

import casp.web.backend.common.base.BaseRepository;
import casp.web.backend.common.enums.EntityStatus;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@JaversSpringDataAuditable
public interface MemberRepository extends BaseRepository<Member>, MemberCustomRepository {

    Page<Member> findAllByEntityStatus(EntityStatus entityStatus, Pageable pageable);

    Optional<Member> findOneByEmail(String email);

    Set<Member> findAllByIdInAndEntityStatus(Set<UUID> membersId, EntityStatus entityStatus);
}
