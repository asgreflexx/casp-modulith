package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.BaseEventRequiredFields;
import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.calendar.data.participants.BaseParticipant;
import casp.web.backend.common.base.BaseView;
import casp.web.backend.common.reference.MemberReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
abstract class BaseEventRead<P extends BaseParticipant> extends BaseView implements BaseEventRequiredFields<P> {
    private BaseEventType eventType;
    protected String name;
    protected String description;
    protected String location;
    protected MemberReference member;
    protected RecurrenceOption recurrenceOption;
    protected LocalDateTime minTime;
    protected LocalDateTime maxTime;
    protected List<CalendarEntry> calendarEntries;
    protected Set<P> participants = new HashSet<>();

    BaseEventRead(BaseEventType eventType) {
        this.eventType = eventType;
    }
}
