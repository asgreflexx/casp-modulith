package casp.web.backend.business.logic.layer.interfaces;


import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.enumerations.Roles;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IMemberService {

    Collection<Member> getMembersByString(String firstName, String lastName);

    List<Roles> listAllAvailableRoles();

    Page<Member> getMembersByPage(Integer page, String numberOfElements, EntityStatus entityStatus) throws Exception;

    Member getMemberById(UUID id) throws Exception;


    Member saveMember(Member member) throws Exception;

    void deleteMemberById(UUID id) throws Exception;

    Member inactiveMember(UUID id) throws Exception;

    Member activateMember(UUID id) throws Exception;

    List<Member> searchMembersByFirstNameOrLastName(String name);

    Set<String> getMembersEmailByIds(Set<UUID> membersId);
}
