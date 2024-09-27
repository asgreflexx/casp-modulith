package casp.web.backend.presentation.layer.event.facades;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.calendar.CalendarService;
import casp.web.backend.business.logic.layer.event.participants.EventParticipantService;
import casp.web.backend.business.logic.layer.event.types.EventService;
import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.types.Event;
import casp.web.backend.presentation.layer.dtos.event.types.EventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static casp.web.backend.presentation.layer.dtos.event.calendar.CalendarMapper.CALENDAR_MAPPER;
import static casp.web.backend.presentation.layer.dtos.event.types.EventMapper.EVENT_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventFacadeImplTest {
    @Mock
    private EventParticipantService eventParticipantService;
    @Mock
    private CalendarService calendarService;
    @Mock
    private EventService eventService;

    @Spy
    @InjectMocks
    private EventFacadeImpl eventFacade;

    @Test
    void getOneById() {
        var event = TestFixture.createEvent();
        when(eventService.getOneById(event.getId())).thenReturn(event);

        var eventDto = eventFacade.getOneById(event.getId());

        assertEquals(event.getId(), eventDto.getId());
        verify(eventFacade).mapDocumentToDto(event);
    }

    @Test
    void deleteById() {
        var id = UUID.randomUUID();
        eventFacade.deleteById(id);

        verify(eventService).deleteById(id);
    }

    @Test
    void getAllByYear() {
        int year = 2023;
        var pageable = Pageable.unpaged();
        var event = TestFixture.createEvent();
        when(eventService.getBaseEventsAsPage(year, pageable)).thenReturn(new PageImpl<>(List.of(event)));

        var eventPage = eventFacade.getAllByYear(year, pageable);

        assertThat(eventPage.getContent())
                .singleElement()
                .satisfies(eventDto -> assertEquals(event.getId(), eventDto.getId()));
    }

    @Nested
    class Save {
        @Captor
        private ArgumentCaptor<Event> eventCaptor;
        @Captor
        private ArgumentCaptor<List<Calendar>> calendarListCaptor;
        @Captor
        private ArgumentCaptor<Set<UUID>> idSetCaptor;

        private EventDto eventDto;
        private Event event;

        @BeforeEach
        void setUp() {
            event = TestFixture.createEvent();
            eventDto = EVENT_MAPPER.toDto(event);
        }

        @Test
        void replaceCalendarEntries() {
            var calendarEntry = TestFixture.createCalendarEntry();
            var calendarDtoList = CALENDAR_MAPPER.toDtoList(List.of(calendarEntry));
            eventDto.setCalendarEntries(calendarDtoList);

            eventFacade.save(eventDto);

            verify(calendarService).replaceCalendarEntries(eventCaptor.capture(), calendarListCaptor.capture());
            assertEquals(event.getId(), eventCaptor.getValue().getId());
            assertThat(calendarListCaptor.getValue()).singleElement().satisfies(actual -> {
                assertEquals(calendarEntry.getEventFrom(), actual.getEventFrom());
                assertEquals(calendarEntry.getEventTo(), actual.getEventTo());
            });
        }

        @Test
        void replaceParticipants() {
            var participant = TestFixture.createEventParticipant();
            eventDto.setParticipantsIdToWrite(Set.of(participant.getId()));

            eventFacade.save(eventDto);

            verify(eventParticipantService).replaceParticipants(eventCaptor.capture(), idSetCaptor.capture());
            assertEquals(event.getId(), eventCaptor.getValue().getId());
            assertThat(idSetCaptor.getValue()).singleElement().isEqualTo(participant.getId());
        }

        @Test
        void saveEvent() {
            eventFacade.save(eventDto);

            verify(eventService).save(eventCaptor.capture());
            assertEquals(event.getId(), eventCaptor.getValue().getId());
        }
    }

    @Nested
    class MapDocumentToDto {
        @Test
        void mapParticipant() {
            var eventParticipant = TestFixture.createEventParticipant();
            var event = eventParticipant.getBaseEvent();
            var member = event.getMember();
            eventParticipant.setMemberOrHandlerId(member.getId());
            eventParticipant.setMember(member);

            when(eventParticipantService.getActiveParticipantsIfMembersOrDogHasHandlerAreActive(event.getId())).thenReturn(Set.of(eventParticipant));

            var eventDto = eventFacade.mapDocumentToDto(event);

            assertThat(eventDto.getParticipantsToRead())
                    .singleElement()
                    .satisfies(actual -> {
                        assertEquals(eventParticipant.getId(), actual.getId());
                        assertEquals(eventParticipant.getMemberOrHandlerId(), actual.getMember().getId());
                    });
        }

        @Test
        void mapDailyEventOption() {
            var dailyEventOption = TestFixture.createDailyEventOption();
            var event = TestFixture.createEvent();
            event.setDailyOption(dailyEventOption);

            var eventDto = eventFacade.mapDocumentToDto(event);

            assertEquals(dailyEventOption.getOptionType(), eventDto.getOption().getOptionType());
        }

        @Test
        void mapWeeklyEventOption() {
            var weeklyEventOption = TestFixture.createWeeklyEventOption();
            var event = TestFixture.createEvent();
            event.setWeeklyOption(weeklyEventOption);

            var eventDto = eventFacade.mapDocumentToDto(event);

            assertEquals(weeklyEventOption.getOptionType(), eventDto.getOption().getOptionType());
        }

        @Test
        void mapWrongBaseEventType() {
            var course = TestFixture.createCourse();

            assertThrows(IllegalArgumentException.class, () -> eventFacade.mapDocumentToDto(course));
        }
    }
}
