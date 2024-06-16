package at.unleashit.caspmodulith.services.calendar.model.participants;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(ExamParticipant.PARTICIPANT_TYPE)
public class ExamParticipant extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "EXAM_PARTICIPANT";

    public ExamParticipant() {
        super(PARTICIPANT_TYPE);
    }
}
