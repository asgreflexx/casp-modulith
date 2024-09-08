package casp.web.backend.presentation.layer.member;

import casp.web.backend.business.logic.layer.member.MemberService;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/member")
@Validated
class MemberRestController {

    private final MemberService memberService;

    @Autowired
    MemberRestController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping()
    ResponseEntity<Page<MemberDto>> getMembers(final @RequestParam EntityStatus entityStatus,
                                               final @ParameterObject Pageable pageable) {
        var memberPage = memberService.getMembersByEntityStatus(entityStatus, pageable);
        return ResponseEntity.ok(MEMBER_MAPPER.toDtoPage(memberPage));
    }

    @GetMapping("/{id}")
    ResponseEntity<MemberDto> getMemberById(final @PathVariable UUID id) {
        return ResponseEntity.ok(MEMBER_MAPPER.toDto(memberService.getMemberById(id)));
    }

    @GetMapping("/search-members-by-firstname-and-lastname")
    ResponseEntity<List<MemberDto>> getMemberByFirstNameAndLastName(final @RequestParam String firstName,
                                                                    final @RequestParam String lastName) {
        var members = memberService.getMembersByFirstNameAndLastName(firstName, lastName);
        return ResponseEntity.ok(MEMBER_MAPPER.toDtoList(members));
    }

    @PostMapping()
    ResponseEntity<MemberDto> saveMember(final @RequestBody @Valid MemberDto memberDto) {
        var member = MEMBER_MAPPER.toDocument(memberDto);
        return ResponseEntity.ok(MEMBER_MAPPER.toDto(memberService.saveMember(member)));
    }

    @DeleteMapping("/{id}")
    void deleteMember(final @PathVariable UUID id) {
        memberService.deleteMemberById(id);
    }

    @PostMapping("/{id}/deactivate")
    ResponseEntity<MemberDto> deactivateMember(final @PathVariable UUID id) {
        return ResponseEntity.ok(MEMBER_MAPPER.toDto(memberService.deactivateMember(id)));
    }

    @PostMapping("/{id}/activate")
    ResponseEntity<MemberDto> activateMember(final @PathVariable UUID id) {
        return ResponseEntity.ok(MEMBER_MAPPER.toDto(memberService.activateMember(id)));
    }

    @GetMapping("/search-members-by-name")
    ResponseEntity<Page<MemberDto>> searchMembersByFirstNameOrLastName(final @RequestParam(required = false, defaultValue = "") String name,
                                                                       final @ParameterObject Pageable pageable) {
        var memberPage = memberService.getMembersByName(name, pageable);
        return ResponseEntity.ok(MEMBER_MAPPER.toDtoPage(memberPage));
    }

    @GetMapping("/roles")
    ResponseEntity<List<Role>> getMemberRoles() {
        return ResponseEntity.ok(memberService.getAllAvailableRoles());
    }

    @GetMapping("/emails-by-ids")
    ResponseEntity<Set<String>> getMembersEmailByIds(final @RequestParam @Size(min = 1) Set<UUID> membersId) {
        return ResponseEntity.ok(memberService.getMembersEmailByIds(membersId));
    }
}