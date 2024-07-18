package at.unleashit.caspmodulith.services.calendar.model.participants;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(EventParticipant.PARTICIPANT_TYPE)
public class EventParticipant extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "EVENT_PARTICIPANT";

    public EventParticipant() {
        super(PARTICIPANT_TYPE);
    }
}
