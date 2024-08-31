package casp.web.backend.business.logic.layer.events.dtos;


import casp.web.backend.data.access.layer.documents.event.Calendar;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Set;

public interface BaseEventDto<P extends BaseParticipant> {
    @NotEmpty
    @Valid
    List<Calendar> getCalendarEntries();

    void setCalendarEntries(@NotEmpty @Valid List<Calendar> calendarEntries);

    @NotNull
    @Valid
    Set<P> getParticipants();

    void setParticipants(@NotNull @Valid Set<P> participants);
}
