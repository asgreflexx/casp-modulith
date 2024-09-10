package casp.web.backend.presentation.layer.member;

import casp.web.backend.business.logic.layer.dog.DogHasHandlerService;
import casp.web.backend.business.logic.layer.member.CardService;
import casp.web.backend.business.logic.layer.member.MemberService;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.Role;
import casp.web.backend.presentation.layer.dtos.member.MemberDto;
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

import static casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.member.CardMapper.CARD_MAPPER;
import static casp.web.backend.presentation.layer.dtos.member.MemberMapper.MEMBER_MAPPER;

@RestController
@RequestMapping("/member")
@Validated
class MemberRestController {

    private final MemberService memberService;
    private final CardService cardService;
    private final DogHasHandlerService dogHasHandlerService;

    @Autowired
    MemberRestController(final MemberService memberService,
                         final CardService cardService,
                         final DogHasHandlerService dogHasHandlerService) {
        this.memberService = memberService;
        this.cardService = cardService;
        this.dogHasHandlerService = dogHasHandlerService;
    }

    @GetMapping()
    ResponseEntity<Page<MemberDto>> getMembers(final @RequestParam EntityStatus entityStatus,
                                               final @ParameterObject Pageable pageable) {
        var memberPage = memberService.getMembersByEntityStatus(entityStatus, pageable);
        return ResponseEntity.ok(MEMBER_MAPPER.toDtoPage(memberPage));
    }

    @GetMapping("/{id}")
    ResponseEntity<MemberDto> getMemberById(final @PathVariable UUID id) {
        var memberDto = MEMBER_MAPPER.toDto(memberService.getMemberById(id));
        var cardDtoSet = CARD_MAPPER.toDtoSet(cardService.getCardsByMemberId(id));
        var dogHasHandlerDtoSet = DOG_HAS_HANDLER_MAPPER.toDtoSet(dogHasHandlerService.getDogHasHandlersByMemberId(id));
        memberDto.setCardDtoSet(cardDtoSet);
        memberDto.setDogHasHandlerDtoSet(dogHasHandlerDtoSet);
        return ResponseEntity.ok(memberDto);
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
        memberService.saveMember(member);
        return getMemberById(member.getId());
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
        memberService.activateMember(id);
        return getMemberById(id);
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
