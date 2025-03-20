package casp.web.backend.member.presentation;

import casp.web.backend.member.MemberService;
import casp.web.backend.member.data.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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

import static casp.web.backend.member.presentation.MemberReadMapper.READ_MAPPER;
import static casp.web.backend.member.presentation.MemberWriteMapper.WRITE_MAPPER;

@RestController
@RequestMapping("member")
@Validated
class MemberRestController {

    private final MemberService memberService;

    @Autowired
    MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    ResponseEntity<Page<MemberRead>> getMembers(@RequestParam EntityStatusParam entityStatusParam,
                                                @RequestParam(required = false, defaultValue = "") String name,
                                                @ParameterObject Pageable pageable) {
        var memberDtoPage = memberService.getMembersByEntityStatusAndName(entityStatusParam.getEntityStatus(), name, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(memberDtoPage));
    }

    @GetMapping("{id}")
    ResponseEntity<MemberRead> getMemberById(@PathVariable UUID id) {
        var memberDto = memberService.getMemberById(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberDto));
    }

    @GetMapping("search-members-by-firstname-and-lastname")
    ResponseEntity<Page<MemberRead>> getMemberByFirstNameAndLastName(@RequestParam @NotBlank String firstName,
                                                                     @RequestParam @NotBlank String lastName,
                                                                     @ParameterObject Pageable pageable) {
        var memberDtoPage = memberService.getMembersByFirstNameAndLastName(firstName, lastName, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(memberDtoPage));
    }

    @PostMapping
    ResponseEntity<MemberRead> saveMember(@RequestBody @Valid MemberWrite memberWrite) {
        var memberDto = WRITE_MAPPER.toSource(memberWrite);
        memberDto = memberService.saveMember(memberDto);
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberDto));
    }

    @DeleteMapping("{id}")
    ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMemberById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/deactivate")
    ResponseEntity<MemberRead> deactivateMember(@PathVariable UUID id) {
        var memberDto = memberService.deactivateMember(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberDto));
    }

    @PostMapping("{id}/activate")
    ResponseEntity<MemberRead> activateMember(@PathVariable UUID id) {
        var memberDto = memberService.activateMember(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberDto));
    }

    @GetMapping("search-members-by-name")
    ResponseEntity<Page<MemberRead>> searchMembersByFirstNameOrLastName(@RequestParam(required = false, defaultValue = "") String name,
                                                                        @ParameterObject Pageable pageable) {
        var memberDtoPage = memberService.getMembersByName(name, pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(memberDtoPage));
    }

    @GetMapping("roles")
    ResponseEntity<List<Role>> getMemberRoles() {
        return ResponseEntity.ok(Role.getAllRolesSorted());
    }

    @GetMapping("emails-by-ids")
    ResponseEntity<Set<String>> getMembersEmailByIds(@RequestParam @Size(min = 1) Set<UUID> membersId) {
        return ResponseEntity.ok(memberService.getMembersEmailByIds(membersId));
    }

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    @PostMapping("migrate-data")
    ResponseEntity<Void> migrateDataToV2() {
        memberService.migrateDataToV2();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("active-members-emails")
    ResponseEntity<Set<String>> getActiveMembersEmail() {
        return ResponseEntity.ok(memberService.getActiveMembersEmail());
    }
}
