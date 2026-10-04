package casp.web.backend.member.presentation;

import casp.web.backend.member.MemberService;
import casp.web.backend.member.MembershipFeesStatsDto;
import casp.web.backend.member.data.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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

import java.util.Set;
import java.util.UUID;

import static casp.web.backend.member.presentation.MemberReadMapper.READ_MAPPER;
import static casp.web.backend.member.presentation.MemberWriteMapper.WRITE_MAPPER;

@RequiredArgsConstructor
@RestController
@RequestMapping("member")
@Validated
class MemberRestController {

    private final MemberService memberService;

    @GetMapping
    ResponseEntity<Page<MemberRead>> getMembers(@RequestParam EntityStatusParam entityStatusParam,
                                                @RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false) Set<Role> roles,
                                                @ParameterObject Pageable pageable) {
        var memberDtoPage = memberService.getMembersByEntityStatusNameAndRoles(entityStatusParam.getEntityStatus(),
                name,
                roles,
                pageable);
        return ResponseEntity.ok(READ_MAPPER.toTargetPage(memberDtoPage));
    }

    @GetMapping("{id}")
    ResponseEntity<MemberRead> getMemberById(@PathVariable UUID id) {
        var memberDto = memberService.getMemberById(id);
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberDto));
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

    @GetMapping("emails-by-ids")
    ResponseEntity<Set<String>> getMembersEmailByIds(@RequestParam @Size(min = 1) Set<UUID> membersId) {
        return ResponseEntity.ok(memberService.getMembersEmailByIds(membersId));
    }

    @GetMapping("active-members-emails")
    ResponseEntity<Set<String>> getActiveMembersEmail() {
        return ResponseEntity.ok(memberService.getActiveMembersEmail());
    }

    @PostMapping("toggle-status/{id}")
    ResponseEntity<MemberRead> toggleStatus(@PathVariable UUID id) {
        return ResponseEntity.ok(READ_MAPPER.toTarget(memberService.toggleStatus(id)));
    }

    @GetMapping("membership-fees-stats")
    ResponseEntity<MembershipFeesStatsDto> getMembershipFeesStats() {
        return ResponseEntity.ok(memberService.getMembershipFeesStats());
    }
}
