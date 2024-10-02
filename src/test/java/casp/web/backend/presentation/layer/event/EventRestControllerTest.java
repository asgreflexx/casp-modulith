package casp.web.backend.presentation.layer.event;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.calendar.CalendarRepository;
import casp.web.backend.data.access.layer.event.participants.BaseParticipantRepository;
import casp.web.backend.data.access.layer.event.types.BaseEventRepository;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.member.Member;
import casp.web.backend.data.access.layer.member.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import casp.web.backend.presentation.layer.dtos.event.types.EventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.EventMapper.EVENT_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventRestControllerTest {
    private static final String EVENT_URL_PREFIX = "/event";
    private static final String DOES_NOT_EXIST_MESSAGE = "This %s[%s] doesn't exist or it isn't active";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BaseEventRepository baseEventRepository;
    @Autowired
    private BaseParticipantRepository baseParticipantRepository;
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        calendarRepository.deleteAll();
        baseParticipantRepository.deleteAll();
        baseEventRepository.deleteAll();
        memberRepository.deleteAll();

        member = TestFixture.createMember();
        memberRepository.save(member);
    }

    @Nested
    class Save {
        @Test
        void invalid() throws Exception {
            var mvcResult = performPost(new EventDto())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            assertThat(mvcResult.getResolvedException())
                    .isNotNull()
                    .satisfies(e -> assertThat(e.getMessage()).contains("NotEmpty.calendarEntries", "NotNull.memberId", "NotBlank.name"));
        }

        @Test
        void valid() throws Exception {
            var event = TestFixture.createEvent();
            var eventDto = EVENT_MAPPER.toDto(event);
            eventDto.setMemberId(member.getId());
            eventDto.setMember(null);
            eventDto.setCalendarEntries(CALENDAR_MAPPER.toDtoList(List.of(TestFixture.createCalendarEntry())));

            performPost(eventDto)
                    .andExpect(status().isNoContent());

            assertThat(baseEventRepository.findAll())
                    .singleElement()
                    .satisfies(c -> assertEquals(eventDto.getMemberId(), c.getMember().getId()));
        }

        private ResultActions performPost(final EventDto eventDto) throws Exception {
            return mockMvc.perform(post(EVENT_URL_PREFIX)
                    .content(MvcMapper.toString(eventDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetOneById {
        @Test
        void doesNotExist() throws Exception {
            var id = UUID.randomUUID();
            var msg = DOES_NOT_EXIST_MESSAGE.formatted(Event.EVENT_TYPE, id);

            performGet(id)
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value(msg));
        }

        @Test
        void exist() throws Exception {
            var event = TestFixture.createEvent();
            event.setMemberId(member.getId());
            baseEventRepository.save(event);

            var mvcResult = performGet(event.getId())
                    .andExpect(status().isOk())
                    .andReturn();

            var eventDto = MvcMapper.toObject(mvcResult, EventDto.class);
            assertEquals(event.getMemberId(), eventDto.getMemberId());
        }

        private ResultActions performGet(final UUID id) throws Exception {
            return mockMvc.perform(get(EVENT_URL_PREFIX + "/{id}", id));
        }
    }

    @Nested
    class DeleteById {
        @Test
        void doesNotExist() throws Exception {
            var id = UUID.randomUUID();
            var msg = DOES_NOT_EXIST_MESSAGE.formatted(Event.EVENT_TYPE, id);

            performDelete(id)
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value(msg));
        }

        @Test
        void exist() throws Exception {
            var event = TestFixture.createEvent();
            var participant = TestFixture.createEventParticipant();
            participant.setBaseEvent(event);
            var calendarEntry = TestFixture.createCalendarEntry();
            calendarEntry.setBaseEvent(event);
            calendarRepository.save(calendarEntry);
            baseParticipantRepository.saveAll(Set.of(participant));
            baseEventRepository.save(event);

            performDelete(event.getId())
                    .andExpect(status().isNoContent());

            assertThat(baseEventRepository.findAll())
                    .singleElement()
                    .satisfies(c -> assertSame(EntityStatus.DELETED, c.getEntityStatus()));

            assertThat(baseParticipantRepository.findAll())
                    .singleElement()
                    .satisfies(p -> assertSame(EntityStatus.DELETED, p.getEntityStatus()));

            assertThat(calendarRepository.findAll())
                    .singleElement()
                    .satisfies(c -> assertSame(EntityStatus.DELETED, c.getEntityStatus()));
        }

        private ResultActions performDelete(final UUID id) throws Exception {
            return mockMvc.perform(delete(EVENT_URL_PREFIX + "/{id}", id));
        }
    }

    @Nested
    class GetAllByYear {
        @Test
        void invalid() throws Exception {
            performGet(-1)
                    .andExpect(status().is4xxClientError())
                    .andExpect(jsonPath("$.message").value("getAllByYear.year: must be greater than 0"));
        }

        @Test
        void eventExist() throws Exception {
            var event = TestFixture.createEvent();
            var now = LocalDateTime.now();
            event.setMaxLocalDateTime(now.plusDays(1));
            event.setMinLocalDateTime(now);
            baseEventRepository.save(event);

            performGet(now.getYear())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(event.getId().toString()));
        }

        private ResultActions performGet(final int year) throws Exception {
            return mockMvc.perform(get(EVENT_URL_PREFIX)
                    .param("page", "0")
                    .param("size", "10")
                    .param("year", Integer.toString(year)));
        }
    }
}
