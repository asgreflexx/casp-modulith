package casp.web.backend.data.access.layer.event.calendar;

import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CalendarRepository extends MongoRepository<Calendar, UUID>, QuerydslPredicateExecutor<Calendar>, CalendarCustomRepository {

    void deleteAllByBaseEventId(UUID baseEventId);

    Optional<Calendar> findByIdAndEntityStatus(UUID id, EntityStatus entityStatus);

    Optional<Calendar> findByIdAndEntityStatusNot(UUID id, EntityStatus entityStatus);

    Set<Calendar> findAllByBaseEventIdAndEntityStatusNot(UUID baseEventId, EntityStatus entityStatus);

    Set<Calendar> findAllByBaseEventIdAndEntityStatus(UUID baseEventId, EntityStatus entityStatus);
}
