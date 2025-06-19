package casp.web.backend.calendar.data;

import casp.web.backend.calendar.data.participants.EventParticipant;
import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.stream.Collectors;


@QueryEntity
@Document
public class Event extends BaseEvent<EventParticipant> {
    public Event() {
        super(BaseEventType.EVENT);
    }

    Set<EventParticipant> getNotDeletedParticipants() {
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
