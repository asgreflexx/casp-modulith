package casp.web.backend.data.access.layer.documents.event.participant;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.StringJoiner;

@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(CoTrainer.PARTICIPANT_TYPE)
public class CoTrainer extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "CO_TRAINER";

    public CoTrainer() {
        super(PARTICIPANT_TYPE);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CoTrainer.class.getSimpleName() + "[", "]")
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
