package casp.web.backend.business.logic.layer.events.types;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.events.calendar.CalendarService;
import casp.web.backend.business.logic.layer.events.mappers.EventMapperImpl;
import casp.web.backend.business.logic.layer.events.participants.EventParticipantService;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.participant.EventParticipant;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.documents.event.types.Event;
import casp.web.backend.data.access.layer.repositories.BaseEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private CalendarService calendarService;
    @Mock
    private EventParticipantService participantService;
    @Mock
    private BaseEventRepository eventRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event event;
    private Calendar calendarEntry;
    private List<Calendar> calendarEntries;
    private Set<EventParticipant> participants;

    @BeforeEach
    void setUp() {
        event = TestFixture.createValidEvent();
        calendarEntry = TestFixture.createValidCalendarEntry(event);
        calendarEntries = List.of(calendarEntry);
        participants = Set.of(TestFixture.createValidEventParticipant(event));
    }

    @Test
    void saveBaseEventDto() {
        var eventMapper = new EventMapperImpl();
        var eventDto = eventMapper.documentToDto(event);
        eventDto.setCalendarEntries(calendarEntries);
        eventDto.setParticipants(participants);
        when(calendarService.replaceCalendarEntriesFromEvent(event, calendarEntries.getFirst())).thenReturn(calendarEntries);
        when(participantService.saveParticipants(eventDto.getParticipants(), event)).thenReturn(eventDto.getParticipants());
        when(eventRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var actualEvent = eventService.saveBaseEventDto(eventDto);

        assertSame(calendarEntry.getEventTo(), actualEvent.getMaxLocalDateTime());
        assertSame(calendarEntry.getEventFrom(), actualEvent.getMinLocalDateTime());
        assertEquals(calendarEntries, actualEvent.getCalendarEntries());
        assertEquals(participants.size(), actualEvent.getParticipantsSize());
        assertEquals(participants, actualEvent.getParticipants());
    }

    @Test
    void deleteBaseEventById() {
        when(eventRepository.findByIdAndEntityStatusNot(event.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(event));

        eventService.deleteBaseEventById(event.getId());

        verify(participantService).deleteParticipantsByBaseEventId(event.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.DELETED, event.getEntityStatus());
    }

    @Test
    void createNewBaseEventWithOneCalendarEntry() {
        var expectedFrom = LocalDateTime.now(ZoneId.systemDefault());
        var expectedTo = expectedFrom.plusHours(1);
        var eventDto = eventService.createNewBaseEventWithOneCalendarEntry();

        assertThat(eventDto.getCalendarEntries())
                .singleElement()
                .satisfies(calendar -> {
                    assertThat(calendar.getEventFrom()).isCloseTo(expectedFrom, within(3, ChronoUnit.SECONDS));
                    assertThat(calendar.getEventTo()).isCloseTo(expectedTo, within(3, ChronoUnit.SECONDS));
                });
    }

    @Test
    void getBaseEventsAsPage() {
        var page = new PageImpl<BaseEvent>(List.of(event));
        var year = LocalDate.now().getYear();
        when(eventRepository.findAllByYearAndEventType(year, event.getEventType(), Pageable.unpaged())).thenReturn(page);

        assertThat(eventService.getBaseEventsAsPage(year, Pageable.unpaged()).getContent()).
                singleElement()
                .isEqualTo(event);
    }

    @Test
    void deleteBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusNotAndEventType(memberId, EntityStatus.DELETED, event.getEventType())).thenReturn(Set.of(event));

        eventService.deleteBaseEventsByMemberId(memberId);

        verify(participantService).deleteParticipantsByBaseEventId(event.getId());
        verify(calendarService).deleteCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.DELETED, event.getEntityStatus());
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.ACTIVE, event.getEventType())).thenReturn(Set.of(event));

        eventService.deactivateBaseEventsByMemberId(memberId);

        verify(participantService).deactivateParticipantsByBaseEventId(event.getId());
        verify(calendarService).deactivateCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.INACTIVE, event.getEntityStatus());
    }

    @Test
    void activateBaseEventsByMemberId() {
        UUID memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndEntityStatusAndEventType(memberId, EntityStatus.INACTIVE, event.getEventType())).thenReturn(Set.of(event));

        eventService.activateBaseEventsByMemberId(memberId);

        verify(participantService).activateParticipantsByBaseEventId(event.getId());
        verify(calendarService).activateCalendarEntriesByBaseEventId(event.getId());
        assertSame(EntityStatus.ACTIVE, event.getEntityStatus());
    }

    @Nested
    class GetBaseEventDtoById {
        @Test
        void eventExist() {
            when(eventRepository.findByIdAndEntityStatus(event.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(event));
            when(calendarService.getCalendarEntriesByBaseEvent(event)).thenReturn(calendarEntries);
            when(participantService.getParticipantsByBaseEventId(event.getId())).thenReturn(participants);

            var eventDto = eventService.getBaseEventDtoById(event.getId());

            assertEquals(calendarEntries, eventDto.getCalendarEntries());
            assertSame(participants, eventDto.getParticipants());
        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> eventService.getBaseEventDtoById(id));
        }
    }
}
