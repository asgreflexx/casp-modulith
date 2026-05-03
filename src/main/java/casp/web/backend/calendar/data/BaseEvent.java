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
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    protected List<CalendarEntry> calendarEntries = new ArrayList<>();
    @Indexed
    protected OffsetDateTime minODT;
    @Indexed
    protected OffsetDateTime maxODT;
    protected Set<P> participants = new HashSet<>();

    protected BaseEvent(BaseEventType eventType) {
        this.eventType = eventType;
    }

    public void addCalendarEntry(CalendarEntry calendarEntry) {
        calendarEntries.add(calendarEntry);
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

    static boolean isMemberNotDeleted(MemberReference member) {
        return EntityStatus.DELETED != member.getEntityStatus();
    }

    static boolean isDogHasHandlerNotDeleted(DogHasHandlerReference dogHasHandler) {
        return EntityStatus.DELETED != dogHasHandler.getEntityStatus()
                && isMemberNotDeleted(dogHasHandler.getMember())
                && EntityStatus.DELETED != dogHasHandler.getDog().getEntityStatus();
    }

    void updateBounds() {
        this.minODT = calendarEntries.stream()
                .map(CalendarEntry::getEntryFromODT)
                .filter(Objects::nonNull) // TODO Remove me when CalendarEntry.entryFrom is removed
                .min(OffsetDateTime::compareTo)
                .orElse(null); // This will never happen because the list is never empty
        this.maxODT = calendarEntries.stream()
                .map(CalendarEntry::getEntryToODT)
                .filter(Objects::nonNull) // TODO Remove me when CalendarEntry.entryTo is removed
                .max(OffsetDateTime::compareTo)
                .orElse(null); // This will never happen because the list is never empty
    }

    abstract Set<P> getNotDeletedParticipants();
}
