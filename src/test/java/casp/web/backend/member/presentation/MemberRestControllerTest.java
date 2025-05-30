package casp.web.backend.member.presentation;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.member.MemberDto;
import casp.web.backend.member.MemberService;
import casp.web.backend.member.TestFixture;
import casp.web.backend.member.data.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.member.MemberMapper.MEMBER_MAPPER;
import static casp.web.backend.member.presentation.MemberReadMapper.READ_MAPPER;
import static casp.web.backend.member.presentation.MemberWriteMapper.WRITE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberRestControllerTest {
    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberRestController memberRestController;
    private MemberDto memberDto;

    @BeforeEach
    void setUp() {
        memberDto = MEMBER_MAPPER.toTarget(TestFixture.createMember());
    }

    @Test
    void getMembers() {
        var name = "name";
        when(memberService.getMembersByEntityStatusAndName(EntityStatus.ACTIVE, name, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(memberDto)));

        var response = memberRestController.getMembers(EntityStatusParam.ACTIVE, name, Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(memberDto));
    }

    @Test
    void getMemberById() {
        when(memberService.getMemberById(memberDto.getId())).thenReturn(memberDto);

        var response = memberRestController.getMemberById(memberDto.getId());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(READ_MAPPER.toTarget(memberDto));
    }

    @Test
    void getMemberByFirstNameAndLastName() {
        when(memberService.getMembersByFirstNameAndLastName(memberDto.getFirstName(), memberDto.getLastName(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(memberDto)));

        var response = memberRestController.getMemberByFirstNameAndLastName(memberDto.getFirstName(), memberDto.getLastName(), Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(memberDto));
    }

    @Test
    void saveMember() {
        when(memberService.saveMember(memberDto)).thenReturn(memberDto);

        var response = memberRestController.saveMember(WRITE_MAPPER.toTarget(memberDto));

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(READ_MAPPER.toTarget(memberDto));
    }

    @Test
    void deleteMember() {
        var response = memberRestController.deleteMember(memberDto.getId());

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(memberService).deleteMemberById(memberDto.getId());
    }

    @Test
    void searchMembersByFirstNameOrLastName() {
        when(memberService.getMembersByName(memberDto.getFirstName(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(memberDto)));

        var response = memberRestController.searchMembersByFirstNameOrLastName(memberDto.getFirstName(), Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(memberDto));
    }

    @Test
    void getMemberRoles() {
        var response = memberRestController.getMemberRoles();

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsSequence(Role.getAllRolesSorted());
    }

    @Test
    void getMembersEmailByIds() {
        when(memberService.getMembersEmailByIds(Set.of(memberDto.getId()))).thenReturn(Set.of(memberDto.getEmail()));

        var response = memberRestController.getMembersEmailByIds(Set.of(memberDto.getId()));

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(memberDto.getEmail());
    }

    @Test
    void migrateDataToV2() {
        var response = memberRestController.migrateDataToV2();

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(memberService).migrateDataToV2();
    }

    @Test
    void getActiveMembersEmail() {
        when(memberService.getActiveMembersEmail()).thenReturn(Set.of(memberDto.getEmail()));

        var response = memberRestController.getActiveMembersEmail();

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(memberDto.getEmail());
    }

    @Test
    void getMembersByNotDogId() {
        var dogId = UUID.randomUUID();
        when(memberService.getMembersByNotDogId(dogId, null, Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(memberDto)));

        var response = memberRestController.getMembersByNotDogId(dogId, null, Pageable.unpaged());
        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(memberDto));
    }

    @Test
    void toggleStatus() {
        when(memberService.toggleStatus(memberDto.getId())).thenReturn(memberDto);

        var response = memberRestController.toggleStatus(memberDto.getId());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(READ_MAPPER.toTarget(memberDto));

    }
}
