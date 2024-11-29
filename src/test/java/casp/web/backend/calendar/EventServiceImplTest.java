package casp.web.backend.calendar;

import casp.web.backend.ReferenceTestFixture;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.Event;
import casp.web.backend.calendar.data.EventRepository;
import casp.web.backend.calendar.data.options.DailyRecurrenceOption;
import casp.web.backend.calendar.data.participants.EventParticipant;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.event.BaseEventMigrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private BaseEventMigrationService migrationService;
    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    private Event event;

    @InjectMocks
    private EventServiceImpl eventService;

    @BeforeEach
    void setUp() {
        event = new Event();
    }

    @Test
    void deleteBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndNotDeleted(memberId)).thenReturn(Set.of(event));

        eventService.deleteBaseEventsByMemberId(memberId);

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.DELETED);
    }

    @Test
    void deactivateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.ACTIVE)).thenReturn(Set.of(event));

        eventService.deactivateBaseEventsByMemberId(memberId);

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.INACTIVE);
    }

    @Test
    void activateBaseEventsByMemberId() {
        var memberId = UUID.randomUUID();
        when(eventRepository.findAllByMemberIdAndStatus(memberId, EntityStatus.INACTIVE)).thenReturn(Set.of(event));

        eventService.activateBaseEventsByMemberId(memberId);

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.ACTIVE);
    }

    @Test
    void migrateDataToV2() {
        var eventSet = Set.of(event);
        when(migrationService.mapToEventV2()).thenReturn(eventSet);

        eventService.migrateDataToV2();

        verify(eventRepository).deleteAll();
        verify(eventRepository).saveAll(eventSet);
    }

    @Test
    void deleteById() {
        when(eventRepository.findOneByIdAndEntityStatus(event.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(event));

        eventService.deleteById(event.getId());

        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getEntityStatus()).isEqualTo(EntityStatus.DELETED);
    }

    @Test
    void getCalendarEntries() {
        var from = LocalDateTime.now().minusDays(1);
        var to = from.plusDays(1);
        var calendarEntry1 = new CalendarEntry(from.minusHours(1), from);
        var calendarEntry2 = new CalendarEntry(from, to);
        var calendarEntry3 = new CalendarEntry(to, to.plusHours(1));
        event.setCalendarEntries(new ArrayList<>(List.of(calendarEntry1, calendarEntry2, calendarEntry3)));
        when(eventRepository.findAllBetweenFromAndTo(from, to)).thenReturn(Stream.of(event));

        var calendarEntryDtoStream = eventService.getCalendarEntriesBetweenFromAndTo(from, to);

        assertThat(calendarEntryDtoStream)
                .singleElement()
                .satisfies(ce -> {
                    assertEquals(calendarEntry2.getEntryFrom(), ce.getEntryFrom());
                    assertEquals(calendarEntry2.getEntryTo(), ce.getEntryTo());
                    assertSame(event.getEventType(), ce.getEventType());
                });
    }

    @Nested
    class GetOneByIdAndCalendarEntryId {

        private CalendarEntry calendarEntry;

        @BeforeEach
        void setUp() {
            calendarEntry = new CalendarEntry(LocalDateTime.MIN, LocalDateTime.MAX);
            event.addCalendarEntry(calendarEntry);
            event.addCalendarEntry(new CalendarEntry(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        }

        @Test
        void eventExist() {
            when(eventRepository.findOneByIdAndEntityStatus(event.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(event));

            var courseDto = eventService.getOneByIdAndCalendarEntryId(event.getId(), calendarEntry.getId());

            assertEquals(event.getId(), courseDto.getId());
            assertThat(courseDto.getCalendarEntries())
                    .singleElement()
                    .satisfies(ce -> {
                        assertEquals(calendarEntry.getEntryFrom(), ce.getEntryFrom());
                        assertEquals(calendarEntry.getEntryTo(), ce.getEntryTo());
                    });
        }

        @Test
        void eventDoesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> eventService.getOneByIdAndCalendarEntryId(id, calendarEntry.getId()));
        }

        @Test
        void calendarEntryDoesNotExist() {
            when(eventRepository.findOneByIdAndEntityStatus(event.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(event));

            assertThrows(NoSuchElementException.class, () -> eventService.getOneByIdAndCalendarEntryId(event.getId(), UUID.randomUUID()));
        }
    }

    @Nested
    class Save {
        private EventDto eventDto;
        private NewCalendarEntryDto newCalendarEntryDto;

        @BeforeEach
        void setUp() {
            newCalendarEntryDto = new NewCalendarEntryDto();
            newCalendarEntryDto.setEntryFrom(LocalDateTime.MIN);
            newCalendarEntryDto.setEntryTo(LocalDateTime.MAX);
            eventDto = new EventDto();
            eventDto.setNewCalendarEntry(newCalendarEntryDto);
        }

        @Test
        void setCalendarEntry() {
            eventService.save(eventDto);

            var actualCourse = getEventSaved();
            assertThat(actualCourse.getCalendarEntries())
                    .singleElement()
                    .satisfies(ce -> {
                        assertEquals(newCalendarEntryDto.getEntryFrom(), ce.getEntryFrom());
                        assertEquals(newCalendarEntryDto.getEntryTo(), ce.getEntryTo());
                    });
            assertEquals(newCalendarEntryDto.getEntryFrom(), actualCourse.getMinTime());
            assertEquals(newCalendarEntryDto.getEntryTo(), actualCourse.getMaxTime());
        }

        @Test
        void setRecurrenceOption() {
            eventDto.setNewCalendarEntry(null);
            var daily = new DailyRecurrenceOption();
            daily.setStartTime(LocalTime.of(1, 0, 0));
            daily.setEndTime(LocalTime.of(3, 0, 0));
            daily.setStartRecurrence(LocalDate.of(2024, 10, 1));
            daily.setEndRecurrence(LocalDate.of(2024, 10, 3));
            eventDto.setRecurrenceOption(daily);
            var minTime = LocalDateTime.of(daily.getStartRecurrence(), daily.getStartTime());
            var maxTime = LocalDateTime.of(daily.getEndRecurrence(), daily.getEndTime());

            eventService.save(eventDto);

            var actualCourse = getEventSaved();
            assertThat(actualCourse.getCalendarEntries())
                    .hasSize(3);
            assertEquals(minTime, actualCourse.getMinTime());
            assertEquals(maxTime, actualCourse.getMaxTime());
        }

        @Test
        void setNewMember() {
            var memberReference = mockMember();
            eventDto.setNewMemberId(memberReference.getId());

            eventService.save(eventDto);

            assertEquals(memberReference, getEventSaved().getMember());
        }

        @Test
        void keepSameMember() {
            var memberReference = ReferenceTestFixture.createMemberReference();
            eventDto.setMember(memberReference);

            eventService.save(eventDto);

            verifyNoInteractions(memberReferenceRepository);
            assertEquals(memberReference, getEventSaved().getMember());
        }

        @Test
        void updateMember() {
            var actualMember = ReferenceTestFixture.createMemberReference();
            var newMember = mockMember();
            eventDto.setMember(actualMember);
            eventDto.setNewMemberId(newMember.getId());

            eventService.save(eventDto);

            assertEquals(newMember, getEventSaved().getMember());
        }

        @Test
        void memberDoesNotExist() {
            var newMemberId = UUID.randomUUID();
            eventDto.setNewMemberId(newMemberId);
            when(memberReferenceRepository.findOneByIdAndEntityStatus(newMemberId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> eventService.save(eventDto));
        }

        @Test
        void addParticipant() {
            var memberReference = mockMember();
            var participant = new EventParticipant(memberReference);
            eventDto.getNewParticipants().add(participant.getId());

            eventService.save(eventDto);

            var actualEvent = getEventSaved();
            assertThat(actualEvent.getParticipants())
                    .singleElement()
                    .isEqualTo(participant);
        }

        private Event getEventSaved() {
            verify(eventRepository).save(eventCaptor.capture());
            return eventCaptor.getValue();
        }

        private MemberReference mockMember() {
            var memberReference = ReferenceTestFixture.createMemberReference();
            when(memberReferenceRepository.findOneByIdAndEntityStatus(memberReference.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(memberReference));
            return memberReference;
        }
    }
}
