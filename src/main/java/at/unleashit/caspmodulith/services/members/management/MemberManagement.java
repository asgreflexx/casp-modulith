package at.unleashit.caspmodulith.services.members.management;

import at.unleashit.caspmodulith.services.MemberStatusChangeEvent;
import at.unleashit.caspmodulith.services.members.MemberDTO;
import at.unleashit.caspmodulith.services.members.MemberExternalAPI;
import at.unleashit.caspmodulith.services.members.MemberInternalAPI;
import at.unleashit.caspmodulith.enums.EntityStatus;
import at.unleashit.caspmodulith.services.members.enums.Roles;
import at.unleashit.caspmodulith.services.members.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class MemberManagement implements MemberExternalAPI, MemberInternalAPI {

    private final ApplicationEventPublisher eventPublisher;
    private final MemberRepository memberRepository;

    @Autowired
    public MemberManagement(ApplicationEventPublisher eventPublisher, MemberRepository memberRepository) {
        this.eventPublisher = eventPublisher;
        this.memberRepository = memberRepository;
    }

    @Override
    public Page<MemberDTO> getMembersByPage(Integer page, String numberOfElements, EntityStatus entityStatus) throws Exception {
        return null;
    }

    @Override
    public MemberDTO getMemberById(UUID id) {
        return null;
    }

    @Override
    public Collection<MemberDTO> getMembersByNames(String firstName, String lastName) {
        return null;
    }

    @Override
    public MemberDTO saveMember(MemberDTO memberDTO) {
        return null;
    }

    @Override
    public void deleteMemberById(UUID id) {
        eventPublisher.publishEvent(new MemberStatusChangeEvent(id, EntityStatus.DELETED));
        memberRepository.deleteById(id);
    }

    @Override
    public void deactivateMember(UUID id) {

    }

    @Override
    public void activateMember(UUID id) {

    }

    @Override
    public Collection<MemberDTO> searchMembersByFirstNameOrLastName(String name) {
        return null;
    }

    @Override
    public List<Roles> listAllAvailableRoles() {
        return null;
    }

    @Override
    public Set<String> getMembersEmailsByIds(Set<UUID> memberIds) {
        return null;
    }
}
