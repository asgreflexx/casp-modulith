package casp.web.backend.calendar.data;

import casp.web.backend.calendar.BaseEventRequiredFields;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.calendar.data.participants.BaseParticipant;
import casp.web.backend.common.base.BaseDocument;
import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogHasHandlerReference;
import casp.web.backend.common.reference.MemberReference;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public abstract class BaseEvent<P extends BaseParticipant> extends BaseDocument implements BaseEventRequiredFields<P> {
    protected BaseEventType eventType;
    protected String name;
    protected String description;
    protected String location;
    @NotNull
    @DBRef
    protected MemberReference member;
    protected RecurrenceOption recurrenceOption;
    @Deprecated(forRemoval = true, since = "2026-04-23")
    protected LocalDateTime minTime;
    @Deprecated(forRemoval = true, since = "2026-04-23")
    protected LocalDateTime maxTime;
    protected OffsetDateTime minTimeODT;
    protected OffsetDateTime maxTimeODT;

    protected List<CalendarEntry> calendarEntries = new ArrayList<>();
    protected Set<P> participants = new HashSet<>();

    protected BaseEvent(BaseEventType eventType) {
        this.eventType = eventType;
    }

    static boolean isMemberNotDeleted(MemberReference member) {
        return EntityStatus.DELETED != member.getEntityStatus();
    }

    static boolean isDogHasHandlerNotDeleted(DogHasHandlerReference dogHasHandler) {
        return EntityStatus.DELETED != dogHasHandler.getEntityStatus()
                && isMemberNotDeleted(dogHasHandler.getMember())
                && EntityStatus.DELETED != dogHasHandler.getDog().getEntityStatus();
    }

    @Override
    public void setCalendarEntries(List<CalendarEntry> calendarEntries) {
        calendarEntries.sort(CalendarEntry::compareTo);
        this.calendarEntries = calendarEntries;
        minTime = calendarEntries.getFirst().getEntryFrom();
        maxTime = calendarEntries.getLast().getEntryTo();
        minTimeODT = calendarEntries.getFirst().getEntryFromODT();
        maxTimeODT = calendarEntries.getLast().getEntryToODT();
    }

    public void addCalendarEntry(CalendarEntry calendarEntry) {
        calendarEntries.add(calendarEntry);
        setCalendarEntries(calendarEntries);
    }

    @Override
    public Set<P> getParticipants() {
        return getNotDeletedParticipants();
    }

    public void addParticipants(Set<P> newParticipants) {
        var notDeletedParticipants = getNotDeletedParticipants();
        notDeletedParticipants.addAll(newParticipants);
        this.participants = notDeletedParticipants;
    }

    abstract Set<P> getNotDeletedParticipants();
}
