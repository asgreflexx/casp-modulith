package casp.web.backend.deprecated.event.calendar;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface CalendarRepository extends MongoRepository<Calendar, UUID> {

    List<Calendar> findAllByBaseEventId(UUID baseEventId, Sort sort);
}
