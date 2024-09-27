package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.data.access.layer.dog.DogHasHandler;
import casp.web.backend.data.access.layer.event.types.Exam;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@QueryEntity
@Document(BaseParticipant.COLLECTION)
@TypeAlias(ExamParticipant.PARTICIPANT_TYPE)
public class ExamParticipant extends BaseParticipant {
    public static final String PARTICIPANT_TYPE = "EXAM_PARTICIPANT";

    @Valid
    @DBRef
    private DogHasHandler dogHasHandler;

    public ExamParticipant() {
        super(PARTICIPANT_TYPE);
    }

    public ExamParticipant(final Exam exam, final DogHasHandler dogHasHandler) {
        super(PARTICIPANT_TYPE, dogHasHandler.getId(), exam);
        this.dogHasHandler = dogHasHandler;
    }

    public DogHasHandler getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(final DogHasHandler dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }
}
