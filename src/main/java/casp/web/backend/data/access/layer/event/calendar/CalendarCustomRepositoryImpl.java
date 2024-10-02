package casp.web.backend.data.access.layer.event.calendar;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.support.SpringDataMongodbQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Component
class CalendarCustomRepositoryImpl implements CalendarCustomRepository {
    private static final QCalendar CALENDAR = QCalendar.calendar;
    private final MongoOperations mongoOperations;

    @Autowired
    CalendarCustomRepositoryImpl(final MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    private static boolean checkIfCalendarEntryIsOfEventTypes(final Calendar calendarEntry, final Set<String> eventTypes) {
        return CollectionUtils.isEmpty(eventTypes) || eventTypes.contains(calendarEntry.getBaseEvent().getEventType());
    }

    @Override
    public List<Calendar> findAllBetweenEventFromAndEventToAndEventTypes(final LocalDate eventFrom, final LocalDate eventTo, final Set<String> eventTypes) {
        var from = LocalDateTime.of(eventFrom, LocalTime.MIN);
        var to = LocalDateTime.of(eventTo, LocalTime.MAX);
        var expression = CALENDAR.entityStatus.eq(EntityStatus.ACTIVE)
                .and(CALENDAR.eventFrom.goe(from))
                .and(CALENDAR.eventTo.loe(to));

        return calendarQuery().where(expression).stream()
                .filter(calendarEntry -> checkIfCalendarEntryIsOfEventTypes(calendarEntry, eventTypes))
                .sorted()
                .toList();
    }

    private SpringDataMongodbQuery<Calendar> calendarQuery() {
        return new SpringDataMongodbQuery<>(mongoOperations, Calendar.class);
    }
}
