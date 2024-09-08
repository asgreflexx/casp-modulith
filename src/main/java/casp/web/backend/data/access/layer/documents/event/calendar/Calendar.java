package casp.web.backend.data.access.layer.documents.event.calendar;

import casp.web.backend.data.access.layer.documents.commons.BaseDocument;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@QueryEntity
@Document
@CalendarFromToConstraint
public class Calendar extends BaseDocument implements Comparable<Calendar> {

    @NotNull
    private LocalDateTime eventFrom;

    @NotNull
    private LocalDateTime eventTo;

    private String location;

    @Valid
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
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Calendar.class.getSimpleName() + "[", "]")
                .add("eventFrom=" + eventFrom)
                .add("eventTo=" + eventTo)
                .add("location='" + location + "'")
                .add("baseEvent=" + baseEvent)
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
