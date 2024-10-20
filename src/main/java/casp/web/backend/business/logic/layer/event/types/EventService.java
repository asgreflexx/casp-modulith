package casp.web.backend.business.logic.layer.event.types;


import java.util.UUID;

public interface EventService {

    void save(EventDto eventDto);

    void deleteById(UUID id);

    void deleteBaseEventsByMemberId(UUID memberId);

    void deactivateBaseEventsByMemberId(UUID memberId);

    void activateBaseEventsByMemberId(UUID memberId);

    /**
     * @deprecated It will be removed in #3.
     */
    @Deprecated(forRemoval = true, since = "0.0.0")
    void migrateDataToV2();
}
