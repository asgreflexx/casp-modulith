package at.unleashit.caspmodulith.services.calendar.model.events;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

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
