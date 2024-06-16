package at.unleashit.caspmodulith.services.members;

import at.unleashit.caspmodulith.enums.EntityStatus;
import at.unleashit.caspmodulith.services.members.enums.Roles;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface MemberExternalAPI {
    Page<MemberDTO> getMembersByPage(Integer page, String numberOfElements, EntityStatus entityStatus) throws Exception;

    MemberDTO getMemberById(UUID id);

    Collection<MemberDTO> getMembersByNames(String firstName, String lastName);

    MemberDTO saveMember(MemberDTO memberDTO);

    void deleteMemberById(UUID id);

    void deactivateMember(UUID id);

    void activateMember(UUID id);

    Collection<MemberDTO> searchMembersByFirstNameOrLastName(String name);

    List<Roles> listAllAvailableRoles();

    Set<String> getMembersEmailsByIds(Set<UUID> memberIds);
}
