package casp.web.backend.business.logic.layer.event.participants;


import casp.web.backend.data.access.layer.event.participants.BaseParticipant;
import casp.web.backend.data.access.layer.event.types.BaseEvent;

import java.util.Set;
import java.util.UUID;

public interface BaseParticipantService<P extends BaseParticipant, E extends BaseEvent> {

    /**
     * Deletes all the participants related to the base event and create the new ones.
     *
     * @param baseEvent      instance of BaseEvent
     * @param participantsId set of UUIDs
     */
    void replaceParticipants(E baseEvent, Set<UUID> participantsId);

    Set<P> getParticipantsByBaseEventId(UUID baseEventId);

    /**
     * Set all participants status with the given base event id to delete.
     * This is used when a base event is deleted.
     *
     * @param baseEventId the id of the base event
     */
    void deleteParticipantsByBaseEventId(UUID baseEventId);

    /**
     * Set all participants status with the given base event id to inactive.
     * This is used when a base event is deactivated.
     *
     * @param baseEventId the id of the base event
     */
    void deactivateParticipantsByBaseEventId(UUID baseEventId);

    /**
     * Set all participants status with the given base event id to active.
     * This is used when a base event is activated.
     *
     * @param baseEventId the id of the base event
     */
    void activateParticipantsByBaseEventId(UUID baseEventId);

    /**
     * Set all participants status with the given member or handler id to delete.
     * This is used when a member or handler is deleted.
     *
     * @param memberOrHandlerId the id of the member or handler
     */
    void deleteParticipantsByMemberOrHandlerId(UUID memberOrHandlerId);

    /**
     * Set all participants status with the given member or handler id to inactive.
     * This is used when a member or handler is deactivated.
     *
     * @param memberOrHandlerId the id of the member or handler
     */
    void deactivateParticipantsByMemberOrHandlerId(UUID memberOrHandlerId);

    /**
     * Set all participants status with the given member or handler id to active.
     * This is used when a member or handler is activated.
     *
     * @param memberOrHandlerId the id of the member or handler
     */
    void activateParticipantsByMemberOrHandlerId(UUID memberOrHandlerId);

    /**
     * Get all active participants if the member or handler is also active.
     *
     * @param baseEventId the id of the base event
     * @return set of participants
     */
    Set<P> getActiveParticipantsIfMembersOrDogHasHandlerAreActive(UUID baseEventId);
}
