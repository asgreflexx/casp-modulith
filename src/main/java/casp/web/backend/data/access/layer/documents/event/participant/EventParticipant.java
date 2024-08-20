package casp.web.backend.data.access.layer.documents.event.participant;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.StringJoiner;

@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(EventParticipant.PARTICIPANT_TYPE)
public class EventParticipant extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "EVENT_PARTICIPANT";

    public EventParticipant() {
        super(PARTICIPANT_TYPE);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", EventParticipant.class.getSimpleName() + "[", "]")
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
