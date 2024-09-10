package casp.web.backend.presentation.layer.member;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.enumerations.Role;
import casp.web.backend.data.access.layer.documents.member.Card;
import casp.web.backend.data.access.layer.documents.member.Member;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import casp.web.backend.data.access.layer.repositories.BaseParticipantRepository;
import casp.web.backend.data.access.layer.repositories.CardRepository;
import casp.web.backend.data.access.layer.repositories.DogHasHandlerRepository;
import casp.web.backend.data.access.layer.repositories.DogRepository;
import casp.web.backend.data.access.layer.repositories.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.RestResponsePage;
import casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerDto;
import casp.web.backend.presentation.layer.dtos.member.MemberDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.presentation.layer.dtos.member.MemberMapper.MEMBER_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MemberRestControllerTest {
    private static final String MEMBER_URL_PREFIX = "/member";
    private static final TypeReference<RestResponsePage<MemberDto>> PAGE_TYPE_REFERENCE = new TypeReference<>() {
    };

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;
    @Autowired
    private BaseEventRepository baseEventRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private DogRepository dogRepository;

    private MemberDto john;
    private MemberDto zephyr;
    private Member inactive;
    private Card card;
    private DogHasHandlerDto dogHasHandler;

    @BeforeEach
    void setUp() {
        cardRepository.deleteAll();
        baseParticipantRepository.deleteAll();
        baseEventRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();
        memberRepository.deleteAll();
        dogRepository.deleteAll();

        var johnDocument = memberRepository.save(TestFixture.createValidMember());
        john = MEMBER_MAPPER.toDto(johnDocument);
        zephyr = MEMBER_MAPPER.toDto(memberRepository.save(TestFixture.createValidMember("Zephyr", "Starling")));
        inactive = TestFixture.createValidMember("INACTIVE", "INACTIVE");
        inactive.setEntityStatus(EntityStatus.INACTIVE);
        memberRepository.save(inactive);
        var bonsaiDocument = TestFixture.createValidDog();
        dogRepository.save(bonsaiDocument);
        var hasHandler = TestFixture.createValidDogHasHandler(bonsaiDocument, johnDocument);
        dogHasHandler = DOG_HAS_HANDLER_MAPPER.toDto(dogHasHandlerRepository.save(hasHandler));
        var eventParticipant = TestFixture.createValidEventParticipant();
        eventParticipant.setMemberOrHandlerId(johnDocument.getId());
        var event = eventParticipant.getBaseEvent();
        event.setMember(johnDocument);
        event.setMemberId(johnDocument.getId());
        var coTrainer = TestFixture.createValidCoTrainer();
        coTrainer.setMemberOrHandlerId(johnDocument.getId());
        var course = coTrainer.getBaseEvent();
        course.setMember(johnDocument);
        course.setMemberId(johnDocument.getId());
        card = TestFixture.createValidCard(johnDocument);
        cardRepository.save(card);
        baseEventRepository.saveAll(Set.of(event, course));
        baseParticipantRepository.saveAll(Set.of(eventParticipant, coTrainer));
    }

    @Test
    void getMembers() throws Exception {
        var mvcResult = mockMvc.perform(get(MEMBER_URL_PREFIX)
                        .param("entityStatus", EntityStatus.ACTIVE.name())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        var memberDtoPage = MvcMapper.toObject(mvcResult, PAGE_TYPE_REFERENCE);
        assertThat(memberDtoPage.getContent()).containsExactlyInAnyOrder(john, zephyr);
    }

    @Test
    void getMembersByFirstNameAndLastName() throws Exception {
        TypeReference<List<MemberDto>> typeReference = new TypeReference<>() {
        };
        var mvcResult = mockMvc.perform(get(MEMBER_URL_PREFIX + "/search-members-by-firstname-and-lastname")
                        .param("firstName", john.getFirstName())
                        .param("lastName", john.getLastName()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).containsExactly(john);
    }

    @Test
    void searchMembersByFirstNameOrLastName() throws Exception {
        var mvcResult = mockMvc.perform(get(MEMBER_URL_PREFIX + "/search-members-by-name")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        var memberDtoPage = MvcMapper.toObject(mvcResult, PAGE_TYPE_REFERENCE);
        assertThat(memberDtoPage.getContent()).containsExactlyInAnyOrder(john, zephyr);
    }

    @Test
    void getMemberRoles() throws Exception {
        TypeReference<List<Role>> typeReference = new TypeReference<>() {
        };
        var mvcResult = mockMvc.perform(get(MEMBER_URL_PREFIX + "/roles"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).containsExactlyElementsOf(Role.getAllRolesSorted());
    }

    @Test
    void getMembersEmailByIds() throws Exception {
        TypeReference<List<String>> typeReference = new TypeReference<>() {
        };
        var mvcResult = mockMvc.perform(get(MEMBER_URL_PREFIX + "/emails-by-ids")
                        .param("membersId", john.getId().toString(), zephyr.getId().toString(), inactive.getId().toString()))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(MvcMapper.toObject(mvcResult, typeReference)).containsExactlyInAnyOrder(john.getEmail(), zephyr.getEmail());
    }

    private ResultActions getMemberById(final UUID id) throws Exception {
        return mockMvc.perform(get(MEMBER_URL_PREFIX + "/{id}", id));
    }

    private ResultActions deactivateMember(final UUID memberId) throws Exception {
        return mockMvc.perform(post(MEMBER_URL_PREFIX + "/{id}/deactivate", memberId));
    }

    private void assertCardDtoSet(final MemberDto memberDto) {
        assertThat(memberDto.getCardDtoSet())
                .singleElement()
                .satisfies(cardDto -> {
                    assertEquals(card.getId(), cardDto.getId());
                    assertNull(cardDto.getMemberId());
                    assertNull(cardDto.getMember());
                });
    }

    private void assertDogHasHandlerDtoSet(final MemberDto memberDto) {
        assertThat(memberDto.getDogHasHandlerDtoSet())
                .singleElement()
                .satisfies(dh -> {
                    assertEquals(dh.getId(), dogHasHandler.getId());
                    assertEquals(dh.getDog().getId(), dogHasHandler.getDog().getId());
                    assertNull(dh.getMemberId());
                    assertNull(dh.getMember());
                });
    }

    @Nested
    class ActivateMember {
        @Test
        void cascadeActivateMember() throws Exception {
            deactivateMember(john.getId())
                    .andExpect(status().isOk());

            var mvcResult = activateMember(john.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, MemberDto.class)).satisfies(dto -> {
                assertSame(EntityStatus.ACTIVE, dto.getEntityStatus());
                assertEquals(john, dto);
                assertCardDtoSet(dto);
                assertDogHasHandlerDtoSet(dto);
            });
            assertThat(dogHasHandlerRepository.findAll()).allSatisfy(dh -> assertSame(EntityStatus.ACTIVE, dh.getEntityStatus()));
            assertThat(baseParticipantRepository.findAll()).allSatisfy(p -> assertSame(EntityStatus.ACTIVE, p.getEntityStatus()));
            assertThat(cardRepository.findAll()).allSatisfy(c -> assertSame(EntityStatus.ACTIVE, c.getEntityStatus()));
            assertThat(baseEventRepository.findAll()).allSatisfy(e -> assertSame(EntityStatus.ACTIVE, e.getEntityStatus()));
        }

        @Test
        void memberNotFound() throws Exception {
            activateMember(john.getId()).andExpect(status().isBadRequest());
        }

        private ResultActions activateMember(final UUID memberId) throws Exception {
            return mockMvc.perform(post(MEMBER_URL_PREFIX + "/{id}/activate", memberId));
        }
    }

    @Nested
    class DeactivateMethod {
        @Test
        void cascadeDeactivate() throws Exception {
            var mvcResult = deactivateMember(john.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, MemberDto.class)).satisfies(dto -> {
                assertSame(EntityStatus.INACTIVE, dto.getEntityStatus());
                assertThat(dto.getCardDtoSet()).isEmpty();
            });
            assertThat(dogHasHandlerRepository.findAll()).allSatisfy(dh -> assertSame(EntityStatus.INACTIVE, dh.getEntityStatus()));
            assertThat(baseParticipantRepository.findAll()).allSatisfy(p -> assertSame(EntityStatus.INACTIVE, p.getEntityStatus()));
            assertThat(cardRepository.findAll()).allSatisfy(c -> assertSame(EntityStatus.INACTIVE, c.getEntityStatus()));
            assertThat(baseEventRepository.findAll()).allSatisfy(e -> assertSame(EntityStatus.INACTIVE, e.getEntityStatus()));
        }

        @Test
        void memberNotFound() throws Exception {
            deactivateMember(inactive.getId()).andExpect(status().isBadRequest());
        }

    }

    @Nested
    class deleteMethod {
        @Test
        void cascadeDelete() throws Exception {
            deleteMember(john.getId())
                    .andExpect(status().isOk());

            getMemberById(john.getId()).andExpect(status().isBadRequest());
            assertThat(dogHasHandlerRepository.findAll()).allSatisfy(dh -> assertSame(EntityStatus.DELETED, dh.getEntityStatus()));
            assertThat(baseParticipantRepository.findAll()).allSatisfy(p -> assertSame(EntityStatus.DELETED, p.getEntityStatus()));
            assertThat(cardRepository.findAll()).allSatisfy(c -> assertSame(EntityStatus.DELETED, c.getEntityStatus()));
            assertThat(baseEventRepository.findAll()).allSatisfy(e -> assertSame(EntityStatus.DELETED, e.getEntityStatus()));
        }

        @Test
        void memberNotFound() throws Exception {
            deleteMember(inactive.getId()).andExpect(status().isBadRequest());
        }

        private ResultActions deleteMember(final UUID memberId) throws Exception {
            return mockMvc.perform(delete(MEMBER_URL_PREFIX + "/{id}", memberId));
        }
    }

    @Nested
    class SaveMember {
        @Test
        void memberIsAlwaysAsActiveSaved() throws Exception {
            john.setEntityStatus(EntityStatus.DELETED);
            var mvcResult = performPost(john)
                    .andExpect(status().isOk())
                    .andReturn();

            assertThat(MvcMapper.toObject(mvcResult, MemberDto.class)).satisfies(dto -> {
                assertSame(EntityStatus.ACTIVE, dto.getEntityStatus());
                assertEquals(john, dto);
                assertCardDtoSet(dto);
                assertDogHasHandlerDtoSet(dto);
            });

        }

        @Test
        void newMemberWithExistingEMail() throws Exception {
            var member = TestFixture.createValidMember();
            member.setEmail(john.getEmail());

            performPost(MEMBER_MAPPER.toDto(member))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void newMemberIsInvalid() throws Exception {
            var member = TestFixture.createValidMember();
            member.setEmail(null);

            performPost(MEMBER_MAPPER.toDto(member))
                    .andExpect(status().isBadRequest());
        }

        private ResultActions performPost(final MemberDto memberDto) throws Exception {
            return mockMvc.perform(post(MEMBER_URL_PREFIX)
                    .content(MvcMapper.toString(memberDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetDataById {
        @Test
        void memberExist() throws Exception {
            var mvcResult = getMemberById(john.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            var memberDto = MvcMapper.toObject(mvcResult, MemberDto.class);
            assertThat(memberDto).isEqualTo(john);
            assertCardDtoSet(memberDto);
            assertDogHasHandlerDtoSet(memberDto);
        }

        @Test
        void memberDoesNotExist() throws Exception {
            getMemberById(inactive.getId())
                    .andExpect(status().isBadRequest());
        }
    }
}
