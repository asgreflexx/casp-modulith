package casp.web.backend.presentation.layer.event;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.types.EventDto;
import casp.web.backend.business.logic.layer.event.types.EventService;
import casp.web.backend.business.logic.layer.event.types.NewCalendarEntryDto;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.data.access.layer.event.calendar.CalendarEntry;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.data.access.layer.event.types.EventRepository;
import casp.web.backend.member.data.Member;
import casp.web.backend.member.data.MemberRepository;
import casp.web.backend.presentation.layer.MvcMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventRestControllerTest {
    private static final String EVENT_URL_PREFIX = "/event";
    private static final String EVENT_DOES_NOT_EXIST_MSG = "Event with id %s does not exist or it is not active.";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberReferenceRepository memberReferenceRepository;
    @Autowired
    private EventRepository eventRepository;

    @SpyBean
    private EventService eventService;
    private Event event;
    private Member member;
    private LocalDateTime startDateTime;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        memberRepository.deleteAll();

        member = memberRepository.save(TestFixture.createMember());
        event = new Event();
        event.setName("Test");
        startDateTime = LocalDateTime.now();
        event.addCalendarEntry(new CalendarEntry(startDateTime, startDateTime.plusHours(1)));
        memberReferenceRepository.findById(member.getId()).ifPresent(event::setMember);
        event = eventRepository.save(event);
    }

    @Test
    void migrateDataToV2() throws Exception {
        mockMvc.perform(post(EVENT_URL_PREFIX + "/migrate-data"))
                .andExpect(status().isNoContent());

        verify(eventService).migrateDataToV2();
    }

    @Nested
    class DeleteById {
        @Test
        void isActive() throws Exception {
            performDelete()
                    .andExpect(status().isNoContent());

            verify(eventService).deleteById(event.getId());
        }

        @Test
        void isNotActive() throws Exception {
            event.setEntityStatus(EntityStatus.INACTIVE);
            eventRepository.save(event);

            var exception = performDelete()
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo(EVENT_DOES_NOT_EXIST_MSG.formatted(event.getId()));
        }

        private ResultActions performDelete() throws Exception {
            return mockMvc.perform(delete(EVENT_URL_PREFIX + "/{id}", event.getId()));
        }
    }

    @Nested
    class Save {
        @Captor
        private ArgumentCaptor<EventDto> eventCaptor;
        private EventWrite eventWrite;

        @BeforeEach
        void setUp() {
            var newCalendarEntry = new NewCalendarEntryDto();
            newCalendarEntry.setEntryFrom(startDateTime.plusDays(1));
            newCalendarEntry.setEntryTo(startDateTime.plusDays(1).plusHours(1));
            eventWrite = new EventWrite();
            eventWrite.setName("event");
            eventWrite.setNewMemberId(member.getId());
            eventWrite.setNewCalendarEntry(newCalendarEntry);
        }

        @Test
        void badEvent() throws Exception {
            var badEvent = new EventWrite();
            badEvent.setParticipants(null);
            badEvent.setNewParticipants(null);
            var exception = performPost(badEvent)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            verifyNoInteractions(eventService);
            assertThat(exception)
                    .isNotNull()
                    .message()
                    .contains("NotBlank.name",
                            "A member is needed and the new member id cannot be the same as the actual member.",
                            "Whether it has a calendar entry or a recurrence option.");
        }

        @Test
        void memberDoesNotExist() throws Exception {
            eventWrite.setNewMemberId(UUID.randomUUID());
            var exception = performPost(eventWrite)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo("Member with id %s does not exist or it is not active.".formatted(eventWrite.getNewMemberId()));
        }

        @Test
        void goodEvent() throws Exception {
            performPost(eventWrite)
                    .andExpect(status().isNoContent());

            verify(eventService).save(eventCaptor.capture());
            assertThat(eventCaptor.getValue())
                    .satisfies(actualEvent -> {
                        assertEquals(eventWrite.getName(), actualEvent.getName());
                        assertEquals(eventWrite.getNewMemberId(), actualEvent.getNewMemberId());
                        assertEquals(eventWrite.getNewCalendarEntry().getEntryFrom(), actualEvent.getNewCalendarEntry().getEntryFrom());
                        assertEquals(eventWrite.getNewCalendarEntry().getEntryTo(), actualEvent.getNewCalendarEntry().getEntryTo());
                    });
        }

        private ResultActions performPost(EventWrite eventWrite) throws Exception {
            return mockMvc.perform(post(EVENT_URL_PREFIX)
                    .content(MvcMapper.toString(eventWrite))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    class GetCalendarEntry {
        private UUID calendarEntryId;

        @BeforeEach
        void setUp() {
            calendarEntryId = event.getCalendarEntries().getFirst().getId();
        }

        @Test
        void eventDoesNotExist() throws Exception {
            var eventId = UUID.randomUUID();
            var exception = performGet(eventId, calendarEntryId)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo(EVENT_DOES_NOT_EXIST_MSG.formatted(eventId));
        }

        @Test
        void calendarEntryDoesNotExist() throws Exception {
            var nonExistingCalendarEntry = UUID.randomUUID();
            var exception = performGet(event.getId(), nonExistingCalendarEntry)
                    .andExpect(status().isBadRequest())
                    .andReturn()
                    .getResolvedException();

            assertThat(exception)
                    .isNotNull()
                    .message()
                    .isEqualTo("The Calendar entry with Id %s not found in Event with Id %s".formatted(nonExistingCalendarEntry, event.getId()));
        }

        @Test
        void calendarEntryExist() throws Exception {
            var mvcResult = performGet(event.getId(), calendarEntryId)
                    .andExpect(status().isOk())
                    .andReturn();

            var eventRead = MvcMapper.toObject(mvcResult, EventRead.class);
            assertThat(eventRead.getCalendarEntries())
                    .singleElement()
                    .satisfies(ce -> assertEquals(calendarEntryId, ce.getId()));
        }

        private ResultActions performGet(UUID eventId, Object calendarEntryId) throws Exception {
            return mockMvc.perform(get(EVENT_URL_PREFIX + "/{eventId}/calendar-entry/{calendarEntryId}", eventId, calendarEntryId));
        }
    }
}
