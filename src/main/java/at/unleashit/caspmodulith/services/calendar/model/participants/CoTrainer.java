package at.unleashit.caspmodulith.services.calendar.model.participants;

import com.querydsl.core.annotations.QueryEntity;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(CoTrainer.PARTICIPANT_TYPE)
public class CoTrainer extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "CO_TRAINER";

    public CoTrainer() {
        super(PARTICIPANT_TYPE);
    }
}
