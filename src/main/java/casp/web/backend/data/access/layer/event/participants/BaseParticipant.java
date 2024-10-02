package casp.web.backend.data.access.layer.event.participants;


import casp.web.backend.data.access.layer.commons.BaseDocument;
import casp.web.backend.data.access.layer.enumerations.EventResponse;
import casp.web.backend.data.access.layer.event.TypesRegex;
import casp.web.backend.data.access.layer.event.types.BaseEvent;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.StringJoiner;
import java.util.UUID;

/**
 * This class isn't abstract for the same reason as {@link BaseEvent}
 */
@QueryEntity
@Document(BaseParticipant.COLLECTION)
public class BaseParticipant extends BaseDocument {
    static final String COLLECTION = "BASE_PARTICIPANT";

    @NotNull
    @Pattern(regexp = TypesRegex.BASE_PARTICIPANT_TYPES_REGEX)
    protected String participantType;

    // This id is the id of the member or dog has handler
    @NotNull
    protected UUID memberOrHandlerId;

    @NotNull
    protected EventResponse response = EventResponse.ACCEPTED;

    @Valid
    @DBRef
    protected BaseEvent baseEvent;

    protected BaseParticipant(String participantType) {
        this.participantType = participantType;
    }

    protected BaseParticipant(final String participantType, final UUID memberOrHandlerId, final BaseEvent baseEvent) {
        this.participantType = participantType;
        this.memberOrHandlerId = memberOrHandlerId;
        this.baseEvent = baseEvent;
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
    public String toString() {
        return new StringJoiner(", ", "[", "]")
                .add("participantType='" + participantType + "'")
                .add("memberOrHandlerId=" + memberOrHandlerId)
                .add("response=" + response)
                .add("baseEvent=" + baseEvent)
                .add("id=" + id)
                .add("version=" + version)
                .add("createdBy='" + createdBy + "'")
                .add("created=" + created)
                .add("modifiedBy='" + modifiedBy + "'")
                .add("modified=" + modified)
                .add("entityStatus=" + entityStatus)
                .toString();
    }
}
