package casp.web.backend.business.logic.layer.event.calendar;

import casp.web.backend.TestFixture;
import casp.web.backend.business.logic.layer.event.options.EventOptionServiceUtility;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.calendar.CalendarRepository;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {
    @Mock
    private CalendarRepository calendarRepository;

    @InjectMocks
    private CalendarServiceImpl calendarService;
    private Calendar calendarEntry;
    private BaseEvent baseEvent;

    @BeforeEach
    void setUp() {
        calendarEntry = TestFixture.createValidCalendarEntry();
        baseEvent = calendarEntry.getBaseEvent();
    }

    @Test
    void getCalendarEntriesByPeriodAndEventTypes() {
        when(calendarRepository.findAllBetweenEventFromAndEventToAndEventTypes(calendarEntry.getEventFrom().toLocalDate(), calendarEntry.getEventTo().toLocalDate(), Collections.emptySet())).thenReturn(List.of(calendarEntry));

        assertThat(calendarService.getCalendarEntriesByPeriodAndEventTypes(calendarEntry.getEventFrom().toLocalDate(), calendarEntry.getEventTo().toLocalDate(), Collections.emptySet()))
                .containsExactly(calendarEntry);
    }

    @Nested
    class ReplaceCalendarEntries {

        @Captor
        private ArgumentCaptor<List<Calendar>> calendarCaptor;
        private List<Calendar> newCalendarEntries;

        @BeforeEach
        void setUp() {
            var dailyEventOption = TestFixture.createValidDailyEventOption();
            dailyEventOption.setStartRecurrence(calendarEntry.getEventFrom().toLocalDate());
            dailyEventOption.setEndRecurrence(calendarEntry.getEventFrom().toLocalDate().plusDays(2));
            baseEvent.setDailyOption(dailyEventOption);
            calendarEntry.setBaseEvent(null);
            newCalendarEntries = EventOptionServiceUtility.createCalendarEntries(baseEvent, calendarEntry);
        }

        @Test
        void deleteOldEntries() {
            calendarService.replaceCalendarEntries(baseEvent, List.of(calendarEntry));

            verify(calendarRepository).deleteAllByBaseEventId(baseEvent.getId());
        }

        @Test
        void setMinAndMaxInBaseEventInstance() {
            calendarService.replaceCalendarEntries(baseEvent, List.of(calendarEntry));

            assertEquals(newCalendarEntries.getFirst().getEventFrom(), baseEvent.getMinLocalDateTime());
            assertEquals(newCalendarEntries.getLast().getEventTo(), baseEvent.getMaxLocalDateTime());
        }

        @Test
        void savesCalendarEntries() {
            calendarService.replaceCalendarEntries(baseEvent, List.of(calendarEntry));

            verify(calendarRepository).saveAll(calendarCaptor.capture());
            assertThat(calendarCaptor.getValue()).zipSatisfy(newCalendarEntries, (a, e) -> {
                assertEquals(a.getBaseEvent(), e.getBaseEvent());
                assertEquals(a.getEventFrom(), e.getEventFrom());
                assertEquals(a.getEventTo(), e.getEventTo());
            });
        }
    }

    @Test
    void getCalendarEntriesByBaseEvent() {
        when(calendarRepository.findAllByBaseEventId(baseEvent.getId())).thenReturn(List.of(calendarEntry));

        assertThat(calendarService.getCalendarEntriesByBaseEvent(baseEvent))
                .containsExactly(calendarEntry);
    }

    @Test
    void saveCalendarEntry() {
        when(calendarRepository.save(calendarEntry)).thenAnswer(invocation -> invocation.getArgument(0));

        assertThat(calendarService.saveCalendarEntry(calendarEntry))
                .isSameAs(calendarEntry);
    }

    @Test
    void deleteCalendarEntryById() {
        when(calendarRepository.findByIdAndEntityStatusNot(calendarEntry.getId(), EntityStatus.DELETED)).thenReturn(Optional.of(calendarEntry));

        calendarService.deleteCalendarEntryById(calendarEntry.getId());

        assertSame(EntityStatus.DELETED, calendarEntry.getEntityStatus());
    }

    @Test
    void deleteCalendarEntriesByBaseEventId() {
        var baseEventId = baseEvent.getId();
        when(calendarRepository.findAllByBaseEventIdAndEntityStatusNot(baseEventId, EntityStatus.DELETED))
                .thenReturn(Set.of(calendarEntry));

        calendarService.deleteCalendarEntriesByBaseEventId(baseEventId);

        assertSame(EntityStatus.DELETED, calendarEntry.getEntityStatus());
    }

    @Test
    void deactivateCalendarEntriesByBaseEventId() {
        var baseEventId = baseEvent.getId();
        when(calendarRepository.findAllByBaseEventIdAndEntityStatus(baseEventId, EntityStatus.ACTIVE))
                .thenReturn(Set.of(calendarEntry));

        calendarService.deactivateCalendarEntriesByBaseEventId(baseEventId);

        assertSame(EntityStatus.INACTIVE, calendarEntry.getEntityStatus());
    }

    @Test
    void activateCalendarEntriesByBaseEventId() {
        var baseEventId = baseEvent.getId();
        when(calendarRepository.findAllByBaseEventIdAndEntityStatus(baseEventId, EntityStatus.INACTIVE))
                .thenReturn(Set.of(calendarEntry));

        calendarService.activateCalendarEntriesByBaseEventId(baseEventId);

        assertSame(EntityStatus.ACTIVE, calendarEntry.getEntityStatus());
    }

    @Nested
    class GetCalendarEntryById {
        @Test
        void calendarEntryExists() {
            when(calendarRepository.findByIdAndEntityStatus(calendarEntry.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(calendarEntry));

            assertSame(calendarEntry, calendarService.getCalendarEntryById(calendarEntry.getId()));
        }

        @Test
        void calendarEntryDoesNotExists() {
            var id = UUID.randomUUID();
            when(calendarRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> calendarService.getCalendarEntryById(id));
        }
    }
}
