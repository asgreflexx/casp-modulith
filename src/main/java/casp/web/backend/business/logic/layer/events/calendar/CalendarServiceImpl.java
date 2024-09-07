package casp.web.backend.business.logic.layer.events.calendar;

import casp.web.backend.business.logic.layer.events.options.EventOptionServiceUtility;
import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import casp.web.backend.data.access.layer.repositories.CalendarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Service
class CalendarServiceImpl implements CalendarService {
    private static final Logger LOG = LoggerFactory.getLogger(CalendarServiceImpl.class);
    private final CalendarRepository calendarRepository;

    @Autowired
    CalendarServiceImpl(final CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    @Override
    public Calendar getCalendarEntryById(final UUID id) {
        return calendarRepository.findByIdAndEntityStatus(id, EntityStatus.ACTIVE).orElseThrow(() -> {
            var msg = "Calendar entry with id %s and entity status active not found".formatted(id);
            LOG.error(msg);
            return new NoSuchElementException(msg);
        });
    }

    @Override
    public List<Calendar> getCalendarEntriesByPeriodAndEventTypes(final LocalDate eventFrom, final LocalDate eventTo, final Set<String> eventTypes) {
        return calendarRepository.findAllBetweenEventFromAndEventToAndEventTypes(eventFrom, eventTo, eventTypes);
    }

    @Override
    public List<Calendar> replaceCalendarEntriesFromEvent(final BaseEvent baseEvent, final Calendar calendarEntry) {
        calendarRepository.deleteAllByBaseEventId(baseEvent.getId());
        return EventOptionServiceUtility.createCalendarEntries(baseEvent, calendarEntry);
    }

    @Override
    public List<Calendar> getCalendarEntriesByBaseEvent(final BaseEvent baseEvent) {
        return calendarRepository.findAllByBaseEventId(baseEvent.getId());
    }

    @Override
    public Calendar saveCalendarEntry(final Calendar calendarEntry) {
        return calendarRepository.save(calendarEntry);
    }

    @Transactional
    @Override
    public void deleteCalendarEntryById(final UUID id) {
        calendarRepository.findByIdAndEntityStatusNot(id, EntityStatus.DELETED)
                .ifPresent(calendarEntry -> calendarEntry.setEntityStatus(EntityStatus.DELETED));
    }

    @Override
    public void deleteCalendarEntriesByBaseEventId(final UUID baseEventId) {
        calendarRepository.findAllByBaseEventIdAndEntityStatusNot(baseEventId, EntityStatus.DELETED)
                .forEach(calendarEntry -> saveItWithStatus(calendarEntry, EntityStatus.DELETED));
    }

    @Override
    public void deactivateCalendarEntriesByBaseEventId(final UUID baseEventId) {
        calendarRepository.findAllByBaseEventIdAndEntityStatus(baseEventId, EntityStatus.ACTIVE)
                .forEach(calendarEntry -> saveItWithStatus(calendarEntry, EntityStatus.INACTIVE));
    }

    @Override
    public void activateCalendarEntriesByBaseEventId(final UUID baseEventId) {
        calendarRepository.findAllByBaseEventIdAndEntityStatus(baseEventId, EntityStatus.INACTIVE)
                .forEach(calendarEntry -> saveItWithStatus(calendarEntry, EntityStatus.ACTIVE));
    }

    private void saveItWithStatus(final Calendar calendarEntry, final EntityStatus deleted) {
        calendarEntry.setEntityStatus(deleted);
        calendarRepository.save(calendarEntry);
    }
}
