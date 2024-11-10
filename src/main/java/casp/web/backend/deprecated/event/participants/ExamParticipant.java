package casp.web.backend.deprecated.event.participants;

import casp.web.backend.deprecated.dog.DogHasHandler;
import casp.web.backend.deprecated.event.types.Exam;
import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.Valid;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @deprecated use {@link casp.web.backend.data.access.layer.event.participants.ExamParticipant} instead.It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
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

    public ExamParticipant(Exam exam, DogHasHandler dogHasHandler) {
        super(PARTICIPANT_TYPE, dogHasHandler.getId(), exam);
        this.dogHasHandler = dogHasHandler;
    }

    public DogHasHandler getDogHasHandler() {
        return dogHasHandler;
    }

    public void setDogHasHandler(DogHasHandler dogHasHandler) {
        this.dogHasHandler = dogHasHandler;
    }
}
