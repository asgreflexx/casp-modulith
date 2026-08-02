package casp.web.backend.calendar.data.participants;

import casp.web.backend.common.reference.MemberReference;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Setter
@Getter
public class EventParticipant extends BaseParticipant {
    @Valid
    @NotNull
    @DBRef
    private MemberReference member;

    public EventParticipant() {
        super(BaseParticipantType.EVENT_PARTICIPANT);
    }

    public EventParticipant(MemberReference member) {
        this();
        this.member = member;
    }

    @Override
    public UUID getId() {
        return member.getId();
    }
}
