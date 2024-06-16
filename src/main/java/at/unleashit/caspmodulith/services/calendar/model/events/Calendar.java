package at.unleashit.caspmodulith.services.calendar.model.events;

import at.unleashit.caspmodulith.services.calendar.model.configurations.BaseEntity;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

@QueryEntity
@Document
@CalendarFromToConstraint
public class Calendar extends BaseEntity implements Comparable<Calendar> {

    @NotNull
    private LocalDateTime eventFrom;

    @NotNull
    private LocalDateTime eventTo;

    private String location;

    @DBRef
    private BaseEvent baseEvent;

    public Calendar() {
    }

    public Calendar(LocalDateTime eventFrom, LocalDateTime eventTo, String location, BaseEvent baseEvent) {
        this.eventFrom = eventFrom;
        this.eventTo = eventTo;
        this.location = location;
        this.baseEvent = baseEvent;
        this.entityStatus = baseEvent.getEntityStatus();
    }

    public Calendar(Calendar calendar, BaseEvent baseEvent) {
        this(calendar.eventFrom, calendar.eventTo, calendar.location, baseEvent);
    }

    public LocalDateTime getEventFrom() {
        return eventFrom;
    }

    public void setEventFrom(LocalDateTime eventFrom) {
        this.eventFrom = eventFrom;
    }

    public LocalDateTime getEventTo() {
        return eventTo;
    }

    public void setEventTo(LocalDateTime eventTo) {
        this.eventTo = eventTo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public BaseEvent getBaseEvent() {
        return baseEvent;
    }

    public void setBaseEvent(BaseEvent baseEvent) {
        this.baseEvent = baseEvent;
    }

    @Override
    public int compareTo(Calendar calendar) {
        return eventFrom.compareTo(calendar.eventFrom) + eventTo.compareTo(calendar.eventTo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Calendar that = (Calendar) o;
        return Objects.equals(hashCode(), that.hashCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
