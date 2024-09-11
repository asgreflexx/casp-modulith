package casp.web.backend.business.logic.layer.member;


import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.Role;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.member.Member;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MemberService {

    List<Member> getMembersByFirstNameAndLastName(@Nullable String firstName, @Nullable String lastName);

    List<Role> getAllAvailableRoles();

    Page<Member> getMembersByEntityStatus(EntityStatus entityStatus, Pageable pageable);

    Member getMemberById(UUID id);

    void saveMember(Member member);

    void deleteMemberById(UUID id);

    Member deactivateMember(UUID id);

    void activateMember(UUID id);

    Page<Member> getMembersByName(@Nullable String name, final Pageable pageable);

    Set<String> getMembersEmailByIds(Set<UUID> membersId);

    void setActiveMemberToBaseEvent(BaseEvent baseEvent);
}
