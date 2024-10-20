package casp.web.backend.data.access.layer.event.types;

import casp.web.backend.business.logic.layer.event.types.EventRequiredFields;
import casp.web.backend.common.enums.BaseEventType;
import casp.web.backend.data.access.layer.event.participants.EventParticipant;
import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@QueryEntity
@Document
public class Event extends BaseEvent implements EventRequiredFields {
    private Set<EventParticipant> participants = new HashSet<>();

    public Event() {
        super(BaseEventType.EVENT);
    }

    @Override
    public Set<EventParticipant> getParticipants() {
        return participants
                .stream()
                .filter(p -> isMemberNotDeleted(p.getMember()))
                .collect(Collectors.toSet());
    }

    @Override
    public void setParticipants(Set<EventParticipant> participants) {
        this.participants = participants;
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
