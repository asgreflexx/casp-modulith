package casp.web.backend.data.access.layer.documents.event.participants;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.StringJoiner;

@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(ExamParticipant.PARTICIPANT_TYPE)
public class ExamParticipant extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "EXAM_PARTICIPANT";

    public ExamParticipant() {
        super(PARTICIPANT_TYPE);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExamParticipant.class.getSimpleName() + "[", "]")
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
