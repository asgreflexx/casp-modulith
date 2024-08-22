package casp.web.backend.business.logic.layer.event;

import casp.web.backend.data.access.layer.documents.enumerations.EntityStatus;
import casp.web.backend.data.access.layer.documents.event.Calendar;
import casp.web.backend.data.access.layer.documents.event.participant.BaseParticipant;
import casp.web.backend.presentation.layer.dtos.events.BaseEventDto;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CalendarService {


    <P extends BaseParticipant> BaseEventDto<P> getBaseEventEntryById(UUID calendarId);

    List<Calendar> getCalendarEntriesByPeriodAndEventTypes(LocalDate eventFrom, LocalDate eventTo, @Nullable Set<String> eventTypes);

    /**
     * It will set status of all the events, calendar entries & participants with this id
     * <ol>
     *     <li>Search for all events that aren't deleted and with this member=id</li>
     *     <li>Search for all participants that aren't deleted and with this memberOrHandlerId=id</li>
     *     <li>If there is any event with this id, search for all calendar entries and participants by the events</li>
     *     <li>Set the status of the events, calendar entries & participants found</li>
     * </ol>
     *
     * @param memberId     the id of the member or handler
     * @param entityStatus the status to be set
     */
    void setMemberEntriesAndEventsStatus(UUID memberId, EntityStatus entityStatus);
}
