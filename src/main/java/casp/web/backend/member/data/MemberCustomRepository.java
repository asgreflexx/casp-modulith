package casp.web.backend.member.data;

import casp.web.backend.common.enums.EntityStatus;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface MemberCustomRepository {
    Page<Member> findAllByFirstNameAndLastName(@Nullable String firstName, @Nullable String lastName, Pageable pageable);

    Page<Member> findAllByEntityStatusAndName(EntityStatus entityStatus, @Nullable String name, Pageable pageable);

    Member findByIdAndEntityStatusCustom(UUID id, EntityStatus entityStatus);

    Set<String> findAllActiveMembersEmails();

    Page<Member> findAllByNotDogId(UUID dogReferenceId, @Nullable String name, Pageable pageable);
}
