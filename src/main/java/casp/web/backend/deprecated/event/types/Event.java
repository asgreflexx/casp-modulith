package casp.web.backend.deprecated.event.types;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @deprecated use {@link casp.web.backend.calendar.data.Event} instead. It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@QueryEntity
@Document(BaseEvent.COLLECTION)
@TypeAlias(Event.EVENT_TYPE)
public class Event extends BaseEvent {
    public static final String EVENT_TYPE = "EVENT";

    public Event() {
        super(EVENT_TYPE);
    }
}
