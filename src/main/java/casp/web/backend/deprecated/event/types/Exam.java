package casp.web.backend.deprecated.event.types;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @deprecated use {@link casp.web.backend.calendar.data.Exam} instead. It will be removed in #3.
 */
@Deprecated(forRemoval = true, since = "0.0.0")
@QueryEntity
@Document(BaseEvent.COLLECTION)
@TypeAlias(Exam.EVENT_TYPE)
public class Exam extends BaseEvent {
    public static final String EVENT_TYPE = "EXAM";

    @NotBlank
    private String judgeName;

    public Exam() {
        super(EVENT_TYPE);
    }

    public String getJudgeName() {
        return judgeName;
    }

    public void setJudgeName(String judgeName) {
        this.judgeName = judgeName;
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
