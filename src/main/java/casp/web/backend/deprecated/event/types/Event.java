package casp.web.backend.deprecated.event.types;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.StringJoiner;


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

    @Override
    public String toString() {
        return new StringJoiner(", ", Event.class.getSimpleName() + "[", "]")
                .add("eventType='" + eventType + "'")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("memberId=" + memberId)
                .add("dailyOption=" + dailyOption)
                .add("weeklyOption=" + weeklyOption)
                .add("minLocalDateTime=" + minLocalDateTime)
                .add("maxLocalDateTime=" + maxLocalDateTime)
                .add("participantsSize=" + participantsSize)
                .add("id=" + id)
                .add("version=" + version)
                .add("createdBy='" + createdBy + "'")
                .add("created=" + created)
                .add("modifiedBy='" + modifiedBy + "'")
                .add("modified=" + modified)
                .add("entityStatus=" + entityStatus)
                .toString();
    }
}
