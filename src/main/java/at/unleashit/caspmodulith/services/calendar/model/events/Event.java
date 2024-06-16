package at.unleashit.caspmodulith.services.calendar.model.events;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@QueryEntity
@Document(BaseEvent.COLLECTION)
@TypeAlias(Event.EVENT_TYPE)
public class Event extends BaseEvent {
    public static final String EVENT_TYPE = "EVENT";

    public Event() {
        super(EVENT_TYPE);
    }
}
