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
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static casp.web.backend.calendar.EventMapper.EVENT_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
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
        var calendarEntry = new CalendarEntry(from, to);
        event.addCalendarEntry(calendarEntry);
        when(eventRepository.findAllBetweenFromAndToOrMemberId(from, to, null)).thenReturn(Stream.of(event));

        var calendarEntryDtoStream = eventService.getCalendarEntriesBetweenFromAndToOrMemberId(from, to, null);

        assertThat(calendarEntryDtoStream)
                .singleElement()
                .satisfies(ce -> {
                    assertEquals(calendarEntry.getEntryFrom(), ce.getEntryFrom());
                    assertEquals(calendarEntry.getEntryTo(), ce.getEntryTo());
                    assertSame(event.getEventType(), ce.getEventType());
                });
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
            var memberReference = mockMember();
            eventDto.setMemberId(memberReference.getId());

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
            var memberReference = mockMember();
            eventDto.setMemberId(memberReference.getId());
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
            eventDto.setMemberId(memberReference.getId());

            eventService.save(eventDto);

            assertEquals(memberReference, getEventSaved().getMember());
        }

        @Test
        void memberDoesNotExist() {
            var newMemberId = UUID.randomUUID();
            eventDto.setMemberId(newMemberId);
            when(memberReferenceRepository.findOneByIdAndEntityStatus(newMemberId, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> eventService.save(eventDto));
        }
    }

    @Nested
    class Participants {
        private EventDto eventDto;

        @BeforeEach
        void setUp() {
            var newCalendarEntryDto = new NewCalendarEntryDto();
            newCalendarEntryDto.setEntryFrom(LocalDateTime.MIN);
            newCalendarEntryDto.setEntryTo(LocalDateTime.MAX);
            eventDto = new EventDto();
            eventDto.setMemberId(mockMember().getId());
            eventDto.setNewCalendarEntry(newCalendarEntryDto);
        }

        @Test
        void addNewParticipantToNewEvent() {
            var newParticipant = ReferenceTestFixture.createMemberReference("new", "participant");
            when(memberReferenceRepository.findOneByIdAndEntityStatus(newParticipant.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(newParticipant));
            when(eventRepository.findOneByIdAndEntityStatus(eventDto.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.empty());
            var expectedParticipant = new EventParticipant(newParticipant);
            eventDto.getParticipantIds().add(newParticipant.getId());

            eventService.save(eventDto);

            var actualEvent = getEventSaved();
            assertThat(actualEvent.getParticipants())
                    .singleElement()
                    .isEqualTo(expectedParticipant);
        }

        @Test
        void addNewParticipantToEmptyList() {
            var newParticipant = ReferenceTestFixture.createMemberReference("new", "participant");
            when(memberReferenceRepository.findOneByIdAndEntityStatus(newParticipant.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(newParticipant));
            when(eventRepository.findOneByIdAndEntityStatus(eventDto.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(EVENT_MAPPER.toSource(eventDto)));
            var expectedParticipant = new EventParticipant(newParticipant);
            eventDto.getParticipantIds().add(expectedParticipant.getId());

            eventService.save(eventDto);

            var actualEvent = getEventSaved();
            assertThat(actualEvent.getParticipants())
                    .singleElement()
                    .isEqualTo(expectedParticipant);
        }

        @Test
        void addExistingParticipantToEvent() {
            var existingParticipant = ReferenceTestFixture.createMemberReference("existing", "participant");
            var sourceEvent = EVENT_MAPPER.toSource(eventDto);
            sourceEvent.addParticipants(Set.of(new EventParticipant(existingParticipant)));
            when(eventRepository.findOneByIdAndEntityStatus(eventDto.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(sourceEvent));
            var expectedParticipant = new EventParticipant(existingParticipant);
            eventDto.getParticipantIds().add(expectedParticipant.getId());

            eventService.save(eventDto);

            var actualEvent = getEventSaved();
            assertThat(actualEvent.getParticipants())
                    .singleElement()
                    .isEqualTo(expectedParticipant);
            verify(memberReferenceRepository, never()).findOneByIdAndEntityStatus(existingParticipant.getId(), EntityStatus.ACTIVE);
        }

        @Test
        void replaceExistingParticipantWithNewParticipant() {
            var existingParticipant = ReferenceTestFixture.createMemberReference("existing", "participant");
            var sourceEvent = EVENT_MAPPER.toSource(eventDto);
            sourceEvent.addParticipants(Set.of(new EventParticipant(existingParticipant)));
            when(eventRepository.findOneByIdAndEntityStatus(eventDto.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(sourceEvent));
            var newParticipant = ReferenceTestFixture.createMemberReference("new", "participant");
            when(memberReferenceRepository.findOneByIdAndEntityStatus(newParticipant.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(newParticipant));
            var expectedParticipant = new EventParticipant(newParticipant);
            eventDto.getParticipantIds().add(expectedParticipant.getId());

            eventService.save(eventDto);

            var actualEvent = getEventSaved();
            assertThat(actualEvent.getParticipants())
                    .singleElement()
                    .isEqualTo(expectedParticipant);
        }
    }

    @Nested
    class GetOneById {
        @Test
        void exist() {
            when(eventRepository.findOneByIdAndEntityStatus(event.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(event));

            var eventDto = eventService.getOneById(event.getId());

            assertEquals(event.getId(), eventDto.getId());
        }

        @Test
        void doesNotExist() {
            var id = UUID.randomUUID();
            when(eventRepository.findOneByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> eventService.getOneById(id));
        }
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
