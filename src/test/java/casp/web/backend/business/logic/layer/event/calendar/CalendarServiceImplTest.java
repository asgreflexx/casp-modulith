package casp.web.backend.business.logic.layer.event.calendar;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.event.calendar.Calendar;
import casp.web.backend.data.access.layer.event.calendar.CalendarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @BeforeEach
    void setUp() {
        calendarEntry = TestFixture.createValidCalendarEntry();
    }

    @Test
    void getCalendarEntriesByPeriodAndEventTypes() {
        when(calendarRepository.findAllBetweenEventFromAndEventToAndEventTypes(calendarEntry.getEventFrom().toLocalDate(), calendarEntry.getEventTo().toLocalDate(), Collections.emptySet())).thenReturn(List.of(calendarEntry));

        assertThat(calendarService.getCalendarEntriesByPeriodAndEventTypes(calendarEntry.getEventFrom().toLocalDate(), calendarEntry.getEventTo().toLocalDate(), Collections.emptySet()))
                .containsExactly(calendarEntry);
    }

    @Test
    void replaceCalendarEntriesFromEvent() {
        assertThat(calendarService.replaceCalendarEntriesFromEvent(calendarEntry.getBaseEvent(), calendarEntry))
                .singleElement()
                .satisfies(calendar -> {
                    assertSame(calendarEntry.getBaseEvent(), calendar.getBaseEvent());
                    assertSame(calendarEntry.getEventFrom(), calendar.getEventFrom());
                    assertSame(calendarEntry.getEventTo(), calendar.getEventTo());
                });

        verify(calendarRepository).deleteAllByBaseEventId(calendarEntry.getBaseEvent().getId());
    }

    @Test
    void getCalendarEntriesByBaseEvent() {
        when(calendarRepository.findAllByBaseEventId(calendarEntry.getBaseEvent().getId())).thenReturn(List.of(calendarEntry));

        assertThat(calendarService.getCalendarEntriesByBaseEvent(calendarEntry.getBaseEvent()))
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
        var baseEventId = calendarEntry.getBaseEvent().getId();
        when(calendarRepository.findAllByBaseEventIdAndEntityStatusNot(baseEventId, EntityStatus.DELETED))
                .thenReturn(Set.of(calendarEntry));

        calendarService.deleteCalendarEntriesByBaseEventId(baseEventId);

        assertSame(EntityStatus.DELETED, calendarEntry.getEntityStatus());
    }

    @Test
    void deactivateCalendarEntriesByBaseEventId() {
        var baseEventId = calendarEntry.getBaseEvent().getId();
        when(calendarRepository.findAllByBaseEventIdAndEntityStatus(baseEventId, EntityStatus.ACTIVE))
                .thenReturn(Set.of(calendarEntry));

        calendarService.deactivateCalendarEntriesByBaseEventId(baseEventId);

        assertSame(EntityStatus.INACTIVE, calendarEntry.getEntityStatus());
    }

    @Test
    void activateCalendarEntriesByBaseEventId() {
        var baseEventId = calendarEntry.getBaseEvent().getId();
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
