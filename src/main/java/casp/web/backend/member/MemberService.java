package casp.web.backend.member;


import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.member.data.Role;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface MemberService {


    Page<MemberDto> getMembersByFirstNameAndLastName(@Nullable String firstName, @Nullable String lastName, Pageable pageable);

    Page<MemberDto> getMembersByEntityStatusNameAndRoles(EntityStatus entityStatus, @Nullable String name, @Nullable final Set<Role> roles, Pageable pageable);

    MemberDto getMemberById(UUID id);

    MemberDto saveMember(MemberDto memberDto);

    void deleteMemberById(UUID id);

    Set<String> getMembersEmailByIds(Set<UUID> membersId);


    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    void migrateDataToV2();

    Set<String> getActiveMembersEmail();

    /**
     * All members except this dog's handler.
     *
     * @param dogId to be excluded.
     * @return Members unrelated to this dog.
     */
    Page<MemberDto> getMembersByNotDogId(UUID dogId, @Nullable String name, Pageable pageable);

    MemberDto toggleStatus(UUID id);
}
