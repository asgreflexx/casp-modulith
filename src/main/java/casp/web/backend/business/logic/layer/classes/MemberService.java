package casp.web.backend.business.logic.layer.classes;


import casp.web.backend.business.logic.layer.interfaces.IMemberService;
import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.enumerations.Roles;
import casp.web.backend.data.access.layer.repositories.IMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Member Service
 *
 * @author iris_e
 */

@Service
public class MemberService implements IMemberService {

    private final IMemberRepository memberRepository;

    @Autowired
    MemberService(IMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * returns the firstName and lastName of a member
     *
     * @param firstName firstName of a member
     * @param lastName  lastName of a member
     * @return returns the firstName and lastName
     */
    @Override
    public Collection<Member> getMembersByString(String firstName, String lastName) {
        return this.memberRepository.findAllByFirstNameAndLastName(firstName, lastName);
    }

    @Override
    public List<Roles> listAllAvailableRoles() {

        return Arrays.asList(Roles.class.getEnumConstants());

    }


    /**
     * https://spring.io/guides/gs/managing-transactions/
     * This method is tagged with @Transactional, meaning that any failure causes the entire operation to
     * roll back to its previous state, and to re-throw the original exception.
     * <p>
     * https://www.javacodegeeks.com/2016/05/understanding-transactional-annotation-spring.html
     * 5.1 Transaction readOnly
     * If you don’t explicitly set readOnly attribute to true, you will have read/write transactions.
     * <p>
     * Its always better to explicitly specify the readOnly attribute, as we have noticed some massive
     * performance improvements with Hibernate because of this.
     *
     * @param page         page of members to be shown in Frontend
     * @param entityStatus
     * @return returns a page of members
     */
    @Override
    public Page<Member> getMembersByPage(Integer page, String numberOfElements, EntityStatus entityStatus) {
        Long size;

        if (numberOfElements.equalsIgnoreCase("all")) {
            size = memberRepository.countAllByEntityStatus(entityStatus);
        } else {
            size = Long.parseLong(numberOfElements);
        }
        return memberRepository.findAllByEntityStatusOrderByLastName(entityStatus, PageRequest.of(page, size.intValue()));
    }


    @Override
    public Member getMemberById(UUID id) throws Exception {
        return memberRepository.findById(id).orElseThrow(() ->
        {
            String msg = "Member doesn't exist";
            return new Exception(msg);
        });
    }

    @Transactional
    public Member saveMember(Member member) throws Exception {

        Optional<Member> tmpMember = memberRepository.findMemberByEmail(member.getEmail());
        if (tmpMember.isPresent() && !tmpMember.get().getId().equals(member.getId())) {
            throw new Exception("Email Address is already in use!");
        }
        if (CollectionUtils.isEmpty(member.getRoles())) {
            member.getRoles().add(Roles.USER);
        }
        return memberRepository.save(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMemberById(UUID id) throws Exception {
        Member member = memberRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
        member.setEntityStatus(EntityStatus.DELETED);
        memberRepository.save(member);
    }

    /**
     * If this method fails, the inactiveMemberVoid will also fail, if called from inactiveMemberVoid
     *
     * @param id ID of the member
     * @return the member
     */
    @Override
    public Member inactiveMember(UUID id) throws Exception {
        Member member = memberRepository.findById(id).orElseThrow(() -> new Exception("Member not found"));
        member.setEntityStatus(EntityStatus.INACTIVE);
        return memberRepository.save(member);
    }

    @Override
    public Member activateMember(UUID id) throws Exception {
        Member member = memberRepository.findById(id).orElseThrow(() -> new Exception("Member not found"));
        member.setEntityStatus(EntityStatus.ACTIVE);
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> searchMembersByFirstNameOrLastName(String name) {
        if (!StringUtils.isEmpty(name)) {
            return memberRepository.findAllByFirstNameOrLastName(name, name);
        }
        return memberRepository.findAll();
    }

    @Override
    public Set<String> getMembersEmailByIds(Set<UUID> membersId) {
        return memberRepository.findAllByIdInAndEntityStatus(membersId, EntityStatus.ACTIVE)
                .stream()
                .map(Member::getEmail)
                .collect(Collectors.toSet());
    }
}
