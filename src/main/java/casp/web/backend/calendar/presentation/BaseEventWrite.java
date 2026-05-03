package casp.web.backend.calendar.presentation;

import casp.web.backend.calendar.NewCalendarEntryDto;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.common.base.BaseView;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
abstract class BaseEventWrite extends BaseView implements BaseEventWriteRequiredFields {
    protected String name;
    protected String description;
    protected String location;
    protected UUID memberId;
    protected RecurrenceOption recurrenceOption;
    @JsonSetter(nulls = Nulls.SKIP)
    private Set<UUID> participantIds = new HashSet<>();
    private NewCalendarEntryDto newCalendarEntry;
}
