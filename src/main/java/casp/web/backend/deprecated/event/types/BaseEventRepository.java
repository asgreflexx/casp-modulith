package casp.web.backend.deprecated.event.types;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Set;
import java.util.UUID;

/**
 * @deprecated It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
public interface BaseEventRepository extends MongoRepository<BaseEvent, UUID>, QuerydslPredicateExecutor<BaseEvent> {

    Set<BaseEvent> findAllByEventType(String eventType);
}
