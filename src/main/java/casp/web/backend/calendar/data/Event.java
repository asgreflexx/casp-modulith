package casp.web.backend.calendar.data;

import casp.web.backend.calendar.EventRequiredFields;
import casp.web.backend.calendar.data.participants.EventParticipant;
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
        return getNotDeletedParticipants();
    }

    @Override
    public void setParticipants(Set<EventParticipant> participants) {
        this.participants = participants;
    }

    public void addParticipants(Set<EventParticipant> newParticipants) {
        var notDeletedParticipants = getNotDeletedParticipants();
        notDeletedParticipants.addAll(newParticipants);
        this.participants = notDeletedParticipants;
    }

    private Set<EventParticipant> getNotDeletedParticipants() {
        return participants
                .stream()
                .filter(p -> isMemberNotDeleted(p.getMember()))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
