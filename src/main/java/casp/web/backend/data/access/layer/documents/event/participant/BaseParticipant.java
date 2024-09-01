package casp.web.backend.data.access.layer.documents.event.participant;


import casp.web.backend.data.access.layer.documents.commons.BaseEntity;
import casp.web.backend.data.access.layer.documents.enumerations.EventResponse;
import casp.web.backend.data.access.layer.documents.event.TypesRegex;
import casp.web.backend.data.access.layer.documents.event.types.BaseEvent;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * This class isn't abstract for the same reason as {@link BaseEvent}
 */
@QueryEntity
@Document(BaseParticipant.COLLECTION)
public class BaseParticipant extends BaseEntity {
    public static final String COLLECTION = "BASE_PARTICIPANT";

    @NotNull
    @Pattern(regexp = TypesRegex.BASE_PARTICIPANT_TYPES_REGEX)
    protected String participantType;

    // This id is the id of the member or dog has handler
    @NotNull
    protected UUID memberOrHandlerId;

    @NotNull
    protected EventResponse response = EventResponse.ACCEPTED;

    /**
     * @deprecated use {@link #baseEventId} instead
     */
    @Deprecated
    @Valid
    @DBRef
    protected BaseEvent baseEvent;

    protected UUID baseEventId;

    protected BaseParticipant(String participantType) {
        this.participantType = participantType;
    }

    public String getParticipantType() {
        return participantType;
    }

    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    public EventResponse getResponse() {
        return response;
    }

    public void setResponse(EventResponse status) {
        this.response = status;
    }

    public UUID getMemberOrHandlerId() {
        return memberOrHandlerId;
    }

    public void setMemberOrHandlerId(UUID memberOrHandlerId) {
        this.memberOrHandlerId = memberOrHandlerId;
    }

    public BaseEvent getBaseEvent() {
        return baseEvent;
    }

    public void setBaseEvent(BaseEvent baseEvent) {
        this.baseEvent = baseEvent;
    }

    @Override
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
