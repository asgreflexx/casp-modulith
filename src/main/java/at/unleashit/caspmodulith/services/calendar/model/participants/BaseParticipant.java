package at.unleashit.caspmodulith.services.calendar.model.participants;

import at.unleashit.caspmodulith.enums.EventResponse;
import at.unleashit.caspmodulith.services.calendar.model.configurations.BaseEntity;
import at.unleashit.caspmodulith.services.calendar.model.configurations.TypesRegex;
import at.unleashit.caspmodulith.services.calendar.model.events.BaseEvent;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;
import java.util.UUID;

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

    @DBRef
    protected BaseEvent baseEvent;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BaseParticipant that = (BaseParticipant) o;
        if (baseEvent == null || that.baseEvent == null) return false;

        return Objects.equals(memberOrHandlerId, that.memberOrHandlerId) &&
                Objects.equals(baseEvent.getId(), that.baseEvent.getId());
    }

    @Override
    public int hashCode() {
        return baseEvent == null ? -1 : Objects.hash(memberOrHandlerId, baseEvent.getId());
    }
}
