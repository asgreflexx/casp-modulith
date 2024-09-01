package casp.web.backend.data.access.layer.repositories;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.calendar.Calendar;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CalendarRepository extends MongoRepository<Calendar, UUID>, QuerydslPredicateExecutor<Calendar> {

    List<Calendar> findCalendarsByBaseEventAndEntityStatus(BaseEvent baseEvent, EntityStatus entityStatus);

    Optional<Calendar> findCalendarByEntityStatusAndId(EntityStatus entityStatus, UUID id);

    void deleteCalendarsByBaseEvent(BaseEvent baseEvent);

    /**
     * This used for testing the {@link casp.web.backend.presentation.layer.rest.CalendarRestController#setMemberEntriesAndEventsStatus(UUID, EntityStatus)}
     *
     * @param baseEvent instance of type {@link BaseEvent}
     * @return a set of {@link Calendar} entries
     */
    Set<Calendar> findAllByBaseEvent(BaseEvent baseEvent);

    Set<Calendar> findAllByBaseEventInAndEntityStatusNotLike(Set<BaseEvent> baseEventSet, EntityStatus entityStatus);
}
