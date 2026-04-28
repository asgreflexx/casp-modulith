package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.calendar.data.participants.BaseParticipant;
import casp.web.backend.calendar.presentation.BaseEventWriteRequiredFields;
import casp.web.backend.common.base.BaseDto;
import casp.web.backend.common.reference.MemberReference;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
abstract class BaseEventDto<P extends BaseParticipant> extends BaseDto implements BaseEventWriteRequiredFields, BaseEventRequiredFields<P> {
    protected String name;
    protected String description;
    protected String location;
    protected MemberReference member;
    protected UUID memberId;
    protected RecurrenceOption recurrenceOption;
    protected LocalDateTime minTime;
    protected LocalDateTime maxTime;
    protected List<CalendarEntry> calendarEntries = new ArrayList<>();
    protected Set<P> participants = new HashSet<>();
    private BaseEventType eventType;
    private NewCalendarEntryDto newCalendarEntry;
    private Set<UUID> participantIds = new HashSet<>();

    BaseEventDto(BaseEventType eventType) {
        this.eventType = eventType;
    }
}
