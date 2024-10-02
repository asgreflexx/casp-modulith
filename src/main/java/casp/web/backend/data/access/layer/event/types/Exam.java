package casp.web.backend.data.access.layer.event.types;

import com.querydsl.core.annotations.QueryEntity;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.StringJoiner;


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
    public boolean equals(final Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Exam.class.getSimpleName() + "[", "]")
                .add("judgeName='" + judgeName + "'")
                .add("eventType='" + eventType + "'")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .add("memberId=" + memberId)
                .add("dailyOption=" + dailyOption)
                .add("weeklyOption=" + weeklyOption)
                .add("minLocalDateTime=" + minLocalDateTime)
                .add("maxLocalDateTime=" + maxLocalDateTime)
                .add("participantsSize=" + participantsSize)
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
