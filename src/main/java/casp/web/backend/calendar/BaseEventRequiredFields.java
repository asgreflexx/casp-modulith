package casp.web.backend.calendar;

import casp.web.backend.calendar.data.BaseEventType;
import casp.web.backend.calendar.data.CalendarEntry;
import casp.web.backend.calendar.data.options.RecurrenceOption;
import casp.web.backend.calendar.data.participants.BaseParticipant;
import casp.web.backend.common.reference.MemberReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BaseEventRequiredFields<P extends BaseParticipant> {
    @NotNull
    BaseEventType getEventType();

    void setEventType(@NotNull BaseEventType eventType);

    @NotBlank
    String getName();

    void setName(@NotBlank String name);

    String getDescription();

    void setDescription(String description);

    String getLocation();

    void setLocation(String location);

    @NotNull
    @Valid
    MemberReference getMember();

    void setMember(@NotNull @Valid MemberReference member);

    @Valid
    RecurrenceOption getRecurrenceOption();

    void setRecurrenceOption(@Valid RecurrenceOption recurrenceOption);

    @NotNull
    LocalDateTime getMinTime();

    void setMinTime(@NotNull LocalDateTime minTime);

    @NotNull
    LocalDateTime getMaxTime();

    void setMaxTime(@NotNull LocalDateTime maxTime);

    @NotEmpty
    List<@Valid CalendarEntry> getCalendarEntries();

    void setCalendarEntries(@NotEmpty List<@Valid CalendarEntry> calendarEntries);

    @NotNull
    Set<@Valid P> getParticipants();

    void setParticipants(@NotNull Set<@Valid P> participants);
}
